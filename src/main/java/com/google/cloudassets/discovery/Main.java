package com.google.cloudassets.discovery;

import com.google.cloudassets.discovery.projectobjects.ProjectAssetsMapper;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;
import com.google.cloudassets.discovery.projectobjects.ProjectMutationsList;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.*;
import com.google.common.flogger.FluentLogger;
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlMetadata;
import org.apache.commons.lang3.tuple.Pair;

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

    private static final String GET_TABLES_LIST_QUERY = "SELECT t.table_name "
                                                        + "FROM information_schema.tables AS t "
                                                        + "WHERE t.TABLE_NAME like '%Assets'";
    private static final String GET_PROJECTS_LIST_QUERY = "SELECT t.workspaceId, t.projectId "
                                                        + "FROM Workspace_Project_Table AS t "
                                                        + "WHERE t.isActive = True";

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static Spanner spanner;
    private static DatabaseId db;
    private static DatabaseClient dbClient;
    private static ReadOnlyTransaction readFromDb;
    private static List<String> existingTableNames;


    /**
     * This function initializes all of the mapping and updating of all of the assets for each
     * of the projects in the ProjectId.txt file.
     */
    public static void main(String[] args) {
        SpannerOptions options = SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();

        // Create spanner DatabaseClient
        db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        dbClient = spanner.getDatabaseClient(db);
        readFromDb = dbClient.readOnlyTransaction();
        createTablesIfNotExist();

        // Use spanner DatabaseClient
        updateAllProjectsAssets();

        readFromDb.close();
        spanner.close();
    }

    /*
    This function updates in out spanner db all of the assets for all of the relevant projects.
     */
    private static void updateAllProjectsAssets() {
        for (Pair<String, String> pair : getProjectsList()) {
            String workspaceId = pair.getKey();
            String projectId = pair.getValue();

            updateProjectAssets(workspaceId, projectId);
        }
    }

    /*
    This function receives a specific workspace ID & project ID and updates all of its assets information.
     */
    private static void updateProjectAssets(String workspaceId, String projectId) {
        // Update project config and assets
        ProjectAssetsMapper projectAssets = new ProjectAssetsMapper(new ProjectConfig(workspaceId, projectId));
        ProjectMutationsList projectMutations = new ProjectMutationsList();
        List<Mutation> mutationsToAdd = projectMutations.getMutationList(projectAssets.getAllAssets());

        // We prepare the insertion of the new assets before the deletion of the old ones so
        // that we wont have data loss in case of an error.
        deleteProjectAssets(workspaceId, projectId);
        dbClient.write(mutationsToAdd);
    }

    /*
    This function retrieves the project ids for which this process should run and returns it as a
    List of Pairs in which the key is the workspace id and the value is the project id.
     */
    private static List<Pair<String, String>> getProjectsList() {
        Statement statement = Statement.newBuilder(GET_PROJECTS_LIST_QUERY).build();
        List<Pair<String, String>> projectsList = new ArrayList<>();

        ResultSet resultSet = readFromDb.executeQuery(statement);
        while (resultSet.next()) {
            projectsList.add(Pair.of(resultSet.getString("workspaceId"), resultSet.getString("projectId")));
        }
        return projectsList;
    }

    /*
    This function deletes all of the assets for the given project ID from all of the asset tables
    that already existed before this process began to run (no need to delete from tables that were
    just created by this process).
     */
    private static void deleteProjectAssets(String workspaceId, String projectId) {
        List<Mutation> deleteMutations = new ArrayList<>();

        for (AssetTable table : AssetTable.values()) {
            String tableName = table.getTableName();
            // Only delete values from tables that existed before this process ran
            if (existingTableNames.contains(tableName)) {
                Key projectKey = Key.of(workspaceId, projectId);
                deleteMutations.add(Mutation.delete(tableName, KeySet.range(KeyRange.closedClosed(projectKey, projectKey))));
            }
        }
        dbClient.write(deleteMutations);
    }

    /*
    This function finds asset tables that do not exist and creates them.
     */
    private static void createTablesIfNotExist() {
        existingTableNames = getExistingTableNames();
        List<String> tablesToCreateQueries = new ArrayList<>();

        for (AssetTable table : AssetTable.values()) {
            if (!existingTableNames.contains(table.getTableName())) {
                tablesToCreateQueries.add(table.getTableCreateQuery());
            }
        }

        executeTablesCreation(tablesToCreateQueries);
    }

    /*
    This function executes the tables creation in our spanner db based on the create table queries
    provided in the tablesToCreateQueries list.
     */
    private static void executeTablesCreation(List<String> tablesToCreateQueries) {
        // Create asset tables only if there are new ones
        if (tablesToCreateQueries.size() > 0) {
            DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
            OperationFuture<Void, UpdateDatabaseDdlMetadata> createTables;
            createTables = dbAdminClient.updateDatabaseDdl(db.getInstanceId().getInstance(),
                                        db.getDatabase(), tablesToCreateQueries, null);
            try {
                createTables.get();
            } catch (Exception exception) {
                String error_msg = "Encountered an Exception while creating new asset tables.";
                logger.atInfo().withCause(exception).log(error_msg);
            }
        }
    }

    /*
    This function returns a list of string which represents all of the asset tables which currently
    exist in our spanner db.
     */
    private static List<String> getExistingTableNames() {
        Statement statement = Statement.newBuilder(GET_TABLES_LIST_QUERY).build();
        List<String> existingTableNames = new ArrayList<>();

        ResultSet resultSet = readFromDb.executeQuery(statement);
        while (resultSet.next()) {
            existingTableNames.add(resultSet.getString("table_name"));
        }
        return existingTableNames;
    }
}
