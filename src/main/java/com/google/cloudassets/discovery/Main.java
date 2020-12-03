package com.google.cloudassets.discovery;

import com.google.cloudassets.discovery.AssetDiscoveryExceptions.TableCreationException;
import com.google.cloudassets.discovery.AssetDiscoveryExceptions.TableInsertionException;
import com.google.cloudassets.discovery.projectobjects.ProjectAssetsMapper;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;
import com.google.cloudassets.discovery.projectobjects.ProjectMutationsList;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.*;
import com.google.common.flogger.FluentLogger;
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * The Main class is in charge of initializing all of the backend flow - creating asset tables in
 * spanner db if needed, creating all assets for all of the projects and inserting them into the
 * relevant spanner db tables.
 */
public class Main {
    private static final String SPANNER_PROJECT_ID = "noa-yarden-2020";
    private static final String SPANNER_INSTANCE_ID = "spanner1";
    private static final String SPANNER_DATABASE_ID = "db1";

    private static final String GET_PROJECTS_LIST_QUERY =
            "SELECT workspaceId, p.projectId, s.serviceAccountEmail "
            + "FROM Workspace_Project_Table as p inner join Workspace_Service_Account_Table as s "
            + "USING (workspaceId) WHERE p.isActive = True and p.serviceAccountActive = True";
    private static final String GET_TABLES_LIST_QUERY = "SELECT table_name "
                                                        + "FROM information_schema.tables "
                                                        + "WHERE table_name like '%Assets'";
    // The order by part is important as the main asset table should be created before any other asset table
    private static final String GET_SUPPORTED_TABLES_QUERY = "SELECT DISTINCT assetTableName, isMainTable "
                                                        + "FROM Asset_Tables_Config "
                                                        + "WHERE assetTableName != 'forAllAssets' "
                                                        + "ORDER BY isMainTable DESC";

    private static List<String> existingTableNames;
    private static List<String> newSupportedTableNames;

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static Spanner spanner;
    private static DatabaseId db;
    private static DatabaseClient dbClient;
    private static ReadOnlyTransaction readFromDb;

    /**
     * This function initializes all of the mapping and updating of all of the assets for each
     * of the projects in the ProjectId.txt file.
     * @throws TableCreationException
     * @throws TableInsertionException
     */
    public static void main(String[] args) throws TableCreationException, TableInsertionException {
        initializeDbVars();

        try {
            maintainTables();
            updateAllProjectsAssets();
        } catch (Throwable exception) {
            throw exception;
        } finally {
            closeDbVars();
        }
    }

    /**
     * This function create the spanner DatabaseClient and initializes all of the DB related variables.
     */
    protected static void initializeDbVars() {
        SpannerOptions options = SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
        db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        dbClient = spanner.getDatabaseClient(db);
        readFromDb = dbClient.readOnlyTransaction();
    }

    /**
     * This function closes all of the DB related variables.
     */
    protected static void closeDbVars() {
        readFromDb.close();
        spanner.close();
    }

    /*
    This function updates in out spanner db all of the assets for all of the relevant projects.
     */
    private static void updateAllProjectsAssets() throws TableInsertionException {
        for (ProjectConfig project : getProjectsList()) {
            updateProjectAssets(project);
        }
    }

    /*
    This function retrieves the project ids for which this process should run and returns it as a
    List of ProjectConfigs containing the workspace ID, project ID and ServiceAccount email
    information for each project.
     */
    private static List<ProjectConfig> getProjectsList() {
        List<ProjectConfig> projectsList = new ArrayList<>();

        ResultSet resultSet = executeStringQuery(GET_PROJECTS_LIST_QUERY);
        while (resultSet.next()) {
            projectsList.add(new ProjectConfig(resultSet.getString("workspaceId"),
                                            resultSet.getString("projectId"),
                                            resultSet.getString("serviceAccountEmail")));
        }
        return projectsList;
    }

    /*
    This function receives a specific workspace ID & project ID as a ProjectConfig object and
    updates all of its assets information.
     */
    private static void updateProjectAssets(ProjectConfig project) throws TableInsertionException {
        // Update project config and assets
        ProjectAssetsMapper projectAssets = new ProjectAssetsMapper(project);
        ProjectMutationsList projectMutations = new ProjectMutationsList();
        List<Mutation> mutationsToAdd = projectMutations.getMutationList(projectAssets.getAllAssets());

        // We prepare the insertion of the new assets before the deletion of the old ones so
        // that we wont have data loss in case of an error.
        deleteProjectAssets(project.getWorkspaceId(), project.getProjectId());
        dbClient.write(mutationsToAdd);
    }

    /*
    This function deletes all of the assets for the given project ID from all of the asset tables
    that already existed before this process began to run (no need to delete from tables that were
    just created by this process).
     */
    private static void deleteProjectAssets(String workspaceId, String projectId) {
        List<Mutation> deleteMutations = new ArrayList<>();

        // Only delete values from tables that existed before this process ran
        for (String tableName : existingTableNames) {
            Key projectKey = Key.of(workspaceId, projectId);
            deleteMutations.add(Mutation.delete(tableName, KeySet.range(KeyRange.closedClosed(projectKey, projectKey))));
        }
        dbClient.write(deleteMutations);
    }

    /*
    This function runs all of functions that are responsible for the tables maintenance which are not
    project specific (finding existing table names, finding and creating newly supported tables).
    */
    private static void maintainTables() throws TableCreationException {
        setExistingTableNames();
        // 'existingTableNames' var must be initialized before getNewSupportedTableNames is called
        setNewSupportedTableNames();
        createTablesIfNotExist();
    }

    /*
    This function updates the existingTableNames variable which is a list of strings that represents
    all of the asset tables which currently exist in our spanner db.
    */
    private static void setExistingTableNames() {
        existingTableNames = new ArrayList<>();

        ResultSet resultSet = executeStringQuery(GET_TABLES_LIST_QUERY);
        while (resultSet.next()) {
            existingTableNames.add(resultSet.getString("table_name"));
        }
    }

    /*
    This function updates the newSupportedTableNames variable which is a list of strings that represents
    all of the asset tables which do not currently exist in our spanner db but were added to the
    Asset_Tables_Config table.
    This function relies on existingTableNames variable being properly initialized.
    */
    private static void setNewSupportedTableNames() {
        newSupportedTableNames = new ArrayList<>();

        ResultSet resultSet = executeStringQuery(GET_SUPPORTED_TABLES_QUERY);
        try {
            while (resultSet.next()) {
                String tableName = resultSet.getString("assetTableName");
                if (!existingTableNames.contains(tableName)) {
                    newSupportedTableNames.add(tableName);
                }
            }
        } catch (NullPointerException exception) {
            logger.atInfo().withCause(exception).log("existingTableNames variable was not " +
                    "initialized before calling the setNewSupportedTableNames function.");
        }
    }

    /*
    This function executes the tables creation in our spanner db for tables that do not yet exist.
    */
    private static void createTablesIfNotExist() throws TableCreationException {
        // Create asset tables only if there are new ones
        if (newSupportedTableNames.size() > 0) {
            DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
            OperationFuture<Void, UpdateDatabaseDdlMetadata> createTables;
            createTables = dbAdminClient.updateDatabaseDdl(db.getInstanceId().getInstance(),
                    db.getDatabase(), getCreateTableQueriesList(), null);
            try {
                createTables.get();
            } catch (Exception exception) {
                logger.atInfo().withCause(exception).log("Encountered an Exception while " +
                        "creating new asset tables.");
            }
        }
    }

    /*
    This function returns a list of strings that represent the SQL create table queries to be
    executed in our spanner DB (of the newly added asset kinds).
    */
    private static List<String> getCreateTableQueriesList() throws TableCreationException {
        List<String> createTableQueries = new ArrayList<>();

        for (String tableName : newSupportedTableNames) {
            TableCreation tableCreation = new TableCreation(tableName);
            createTableQueries.add(tableCreation.getCreateTableStatement());
        }
        return createTableQueries;
    }

    /**
     * This function receives a string representing a read only SQL query and executes it in our
     * spanner DB.
     * @param query - a string representing an SQL statement.
     * @return a ResultSet of the query results.
     */
    public static ResultSet executeStringQuery(String query) {
        Statement statement = Statement.newBuilder(query).build();
        return readFromDb.executeQuery(statement);
    }
}
