package ResourceDiscovery;

import ResourceDiscovery.ProjectObjects.ProjectAssetsMapper;
import ResourceDiscovery.ProjectObjects.ProjectConfig;
import ResourceDiscovery.ProjectObjects.ProjectMutationsList;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.*;
import com.google.common.flogger.FluentLogger;
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlMetadata;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
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

    private static final String PROJECTS_ID_FILEPATH = "src/main/resources/ProjectIds.txt";

    private static final JSONParser jsonParser = new JSONParser();
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static Spanner spanner;
    private static DatabaseId db;
    private static DatabaseClient dbClient;


    /**
     * This function initializes all of the mapping and updating of all of the assets for each
     * of the projects in the ProjectId.txt file.
     */
    public static void main(String[] args) {
        SpannerOptions options = SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
        try {
            // create spanner DatabaseClient
            db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
            dbClient = spanner.getDatabaseClient(db);
            createTablesIfNotExist();

            // Use spanner DatabaseClient
            updateProjectsAssets();

        } catch (ParseException exception) {
            String error_msg = "Encountered an ParseException while parsing " + PROJECTS_ID_FILEPATH;
            logger.atInfo().withCause(exception).log(error_msg);
        } catch (IOException exception) {
            String error_msg = "Encountered an IOException while reading " + PROJECTS_ID_FILEPATH;
            logger.atInfo().withCause(exception).log(error_msg);
        } finally {
            spanner.close();
        }
    }

    /*
    This function updates in out spanner db all of the assets for all of the relevant projects.
     */
    private static void updateProjectsAssets() throws IOException, ParseException {
        JSONObject projectJson = (JSONObject) jsonParser.parse(new FileReader(PROJECTS_ID_FILEPATH));
        for (Object key : projectJson.keySet()) {
            String accountId = (String) key;
            String projectId = (String) projectJson.get(key);

            // update project config and assets
            ProjectConfig.getInstance().setNewProject(accountId, projectId);
            ProjectAssetsMapper projectAssets = new ProjectAssetsMapper();
            ProjectMutationsList projectMutations = new ProjectMutationsList();
            dbClient.write(projectMutations.getMutationList(projectAssets.getAllAssets()));
        }
    }

    /*
    This function finds asset tables that do not exist and creates them.
     */
    private static void createTablesIfNotExist() {
        List<String> existingTableNames = getExistingTableNames();
        List<String> tablesToCreateQueries = new ArrayList<>();

        for (AssetTables table : AssetTables.values()) {
            if (!existingTableNames.contains(table.getTableName())) {
                tablesToCreateQueries.add(table.getTableCreateQuery());
            }
        }

        ExecuteTablesCreation(tablesToCreateQueries);
    }

    /*
    This function executes the tables creation in our spanner db based on the create table queries
    provided in the tablesToCreateQueries list.
     */
    private static void ExecuteTablesCreation(List<String> tablesToCreateQueries) {
        // create asset tables only if there are new ones
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

        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                existingTableNames.add(resultSet.getString("table_name"));
            }
        } catch (Exception exception) {
            String error_msg = "Encountered an exception while trying to retrieve all of the" +
                                " asset table names in spanner db.";
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return existingTableNames;
    }
}
