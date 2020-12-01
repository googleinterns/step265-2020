package resourceDisplay;

import com.google.cloud.spanner.*;
import com.google.cloudassets.acounts.CreateWorkspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This Class does all DB access
 */
public class AssetsRepository {
    private static final String SPANNER_STING_FOR_WORKSPACE_ID = "SELECT DISTINCT workspaceId, workspaceDisplayName " +
            "FROM Workspace_User_Table WHERE userEmail = @userID ORDER BY workspaceId";

    /**
     * This is used to check if a workspaceID already exists
     *
     * @param dbClient - A client for connection to the DB
     * @param workspaceId   - The new workspace Id to be created
     * @return a boolean value that says if the workspace already exists
     */
    public static boolean checkIfWorkspaceExists(DatabaseClient dbClient, String workspaceId) {

        Statement statement =
                Statement.newBuilder(
                        "SELECT workspaceId, serviceAccountEmail, workspaceDisplayName "
                                + "FROM Workspace_Service_Account_Table "
                                + "WHERE workspaceId = @workspaceId")
                        .bind("workspaceId")
                        .to(workspaceId)
                        .build();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            if (resultSet.next()) {
                return true;
            }
            return false;
        }

    }

    /**
     * This is used to create a new workspace and service account and add to all relevant tables
     *
     * @param dbClient - A client for connection to the DB
     * @param userId   - The user for whom we want to create a new workspace
     * @param workspaceName   - The new workspace name (display name for user)
     * @param workspaceId   - The new workspace Id (used by us)
     * @param project   - The projects associated with this workspace
     * @param serviceAccount   - The new service account
     */
    public static void addNewWorkspaceAndAccount(DatabaseClient dbClient, String userId,
                                                 String workspaceName, String workspaceId,
                                                 String project, String serviceAccount) {
        List<Mutation> mutations =
                Arrays.asList(
                        Mutation.newInsertBuilder("Workspace_Service_Account_Table")
                                .set("workspaceId")
                                .to(workspaceId)
                                .set("serviceAccountEmail")
                                .to(serviceAccount)
                                .set("workspaceDisplayName")
                                .to(workspaceName)
                                .build(),
                        Mutation.newInsertBuilder("Workspace_User_Table")
                                .set("workspaceId")
                                .to(workspaceId)
                                .set("workspaceDisplayName")
                                .to(workspaceName)
                                .set("userEmail")
                                .to(userId)
                                .build(),
                        Mutation.newInsertBuilder("Workspace_Project_Table")
                                .set("workspaceId")
                                .to(workspaceId)
                                .set("projectId")
                                .to(project)
                                .set("isActive")
                                .to(Boolean.FALSE)
                                .set("serviceAccountActive")
                                .to(Boolean.FALSE)
                                .build());
        dbClient.write(mutations);
    }

    /**
     * This is used to create a list of all workspace values for a specific user
     *
     * @param dbClient - A client for connection to the DB
     * @param userID   - The user for whom we want the list of workspaces
     * @return a list of all workspace values for the specific user
     */
    public List<WorkspaceObject> getWorkspaceIdList(DatabaseClient dbClient, String userID) {
        List<WorkspaceObject> workspaceIdList = new ArrayList<>();
        Statement statement = Statement.newBuilder(SPANNER_STING_FOR_WORKSPACE_ID)
                .bind("userID")
                .to(userID)
                .build();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                WorkspaceObject workspaceObject = new  WorkspaceObject();
                if (!resultSet.isNull("workspaceId")) {
                    workspaceObject.setWorkspaceID(resultSet.getString("workspaceId"));
                }
                if (!resultSet.isNull("workspaceDisplayName")) {
                    workspaceObject.setWorkspaceDisplayName(resultSet.getString("workspaceDisplayName"));
                }
                workspaceIdList.add(workspaceObject);
            }
        }
        return workspaceIdList;
    }

    /**
     * This is used to create a list of all filter values from a project
     *
     * @param dbClient    - A client for connection to the DB
     * @param workspaceId - A workspace for which we want the list of different filter values
     * @param filterType  - The type of filter for which we want the values
     * @return a list of all values of specific filter type
     */
    public List<String> getFilterList(DatabaseClient dbClient, String workspaceId,
                                      String filterType) {
        List<String> filterList = new ArrayList<>();
        String statementString = String.format("SELECT DISTINCT %s FROM Main_Assets WHERE " +
                "workspaceId = @workspaceId ORDER BY %s", filterType, filterType);
        Statement statement = Statement.newBuilder(statementString)
                .bind("workspaceId")
                .to(workspaceId)
                .build();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                if (!resultSet.isNull(filterType)) {
                    filterList.add(resultSet.getString(filterType));
                }
            }
        }
        return filterList;
    }

    /**
     * This is used to execute a query that was built dynamically and column types are unknown
     * and return a list of data
     *
     * @param statement   - A statement that holds the query
     * @param dbClient    - A client for connection to the DB
     * @param columnNames - A list of column names to use for getting real types
     * @param columnTypes - A list of column types to use with Arrays
     * @return a list os lists where each row holds information of one asset in strings (to use
     * in template)
     */
    public List<List<String>> executeQueryAndReturnList(Statement statement,
                                                        DatabaseClient dbClient,
                                                        List<String> columnNames,
                                                        List<String> columnTypes) {
        List<List<String>> allAssets = new ArrayList<>();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 0; i < columnNames.size(); i++) {
                    if (resultSet.isNull(columnNames.get(i))) {
                        row.add("");
                        continue;
                    }
                    Type type = resultSet.getColumnType(columnNames.get(i));
                    switch (type.getCode()) {
                        case ARRAY:
                            String arrayType = columnTypes.get(i);
                            if (arrayType.equals("ARRAY<STRING(MAX)>")) {
                                row.add(String.valueOf(resultSet.getStringList(columnNames.get(i))));
                            }
                            break;
                        case BOOL:
                            row.add(String.valueOf(resultSet.getBoolean(columnNames.get(i))));
                            break;
                        case BYTES:
                            row.add(String.valueOf(resultSet.getBytes(columnNames.get(i))));
                            break;
                        case DATE:
                            row.add(String.valueOf(resultSet.getDate(columnNames.get(i))));
                            break;
                        case FLOAT64:
                            row.add(String.valueOf(resultSet.getDouble(columnNames.get(i))));
                            break;
                        case INT64:
                            row.add(String.valueOf(resultSet.getLong(columnNames.get(i))));
                            break;
                        case NUMERIC:
                            row.add(String.valueOf(resultSet.getBigDecimal(columnNames.get(i))));
                            break;
                        case STRING:
                            row.add(resultSet.getString(columnNames.get(i)));
                            break;
                        case STRUCT:
                            //not used at the moment
                            break;
                        case TIMESTAMP:
                            row.add(String.valueOf(resultSet.getTimestamp(columnNames.get(i))));
                            break;
                    }
                }
                allAssets.add(row);
            }
            return allAssets;
        }
    }

    /**
     * This is used specifically to build and run query to get table schema as a result set
     *
     * @param tableName - The table we whant to know about
     * @param dbClient  - A client for connection to the DB
     * @return ResultSet with columnName, columnDisplayName, columnType from config table
     */
    public ResultSet runConfigQuery(String tableName, DatabaseClient dbClient) {
        Statement statement =
                Statement.newBuilder(
                        "SELECT columnName, columnDisplayName, columnType FROM " +
                                "Asset_Tables_Config "
                                + "WHERE assetTableName = @tableName "
                                + "and toDisplay = True")
                        .bind("tableName")
                        .to(tableName)
                        .build();
        return dbClient.singleUse().executeQuery(statement);
    }

    /**
     * This is used to fill-in the tables for building a TableQueryObject with kind filter
     *
     * @param resultSet               - A ResultSet from query
     * @param columnDisplays          - The columnDisplays list to fill
     * @param columnNames             - The columnNames list to fill
     * @param columnNamesForKindQuery - The columnNamesForKindQuery list to fill
     * @param columnTypes             - The columnTypes list to fill
     * @param columnsForUsing         - The columnsForUsing list to fill
     * @param tableName               - The tableName to use in the join statement
     * @param isForUsing              - A boolean value to know if used in the using part of
     *                                statement
     */
    public void fillInLists(ResultSet resultSet, List<String> columnDisplays,
                            List<String> columnNames, List<String> columnNamesForKindQuery,
                            List<String> columnTypes, List<String> columnsForUsing,
                            String tableName, Boolean isForUsing) {
        while (resultSet.next()) {
            String colName = resultSet.getString("columnName");
            columnDisplays.add(resultSet.getString("columnDisplayName"));
            columnTypes.add(resultSet.getString("columnType"));
            columnNames.add(colName);
            String colNameWithTableName = tableName;
            colNameWithTableName += ".";
            colNameWithTableName += colName;
            columnNamesForKindQuery.add(colNameWithTableName);
            if (isForUsing) {
                columnsForUsing.add(colName);
            }
        }
    }

    /**
     * This is used to create TableQueryObject dynamically for kind
     *
     * @param dbClient - A client for connection to the DB
     * @param kind     - The kind of object we want to filter by
     * @return A TableQueryObject for using when running query
     */
    public TableQueryObject createTableQueryObjectForKind(DatabaseClient dbClient, String kind) {
        List<String> columnDisplays = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        List<String> columnNamesForKindQuery = new ArrayList<>();
        List<String> columnTypes = new ArrayList<>();
        List<String> columnsForUsing = new ArrayList<>();
        String statementString = "";

        ResultSet resultSetForAllAssets = runConfigQuery("forAllAssets", dbClient);
        fillInLists(resultSetForAllAssets, columnDisplays, columnNames, columnNamesForKindQuery,
                columnTypes, columnsForUsing, "Main_Assets", true);

        ResultSet resultSetForMainAssets = runConfigQuery("Main_Assets", dbClient);
        fillInLists(resultSetForMainAssets, columnDisplays, columnNames, columnNamesForKindQuery,
                columnTypes, columnsForUsing, "Main_Assets", false);

        Statement statementForQueryFromKindTable =
                Statement.newBuilder(
                        "SELECT DISTINCT assetTableName FROM Asset_Tables_Config "
                                + "WHERE assetKind = @assetKind")
                        .bind("assetKind")
                        .to(kind)
                        .build();
        try (ResultSet resultSetForQueryForTableName =
                     dbClient.singleUse().executeQuery(statementForQueryFromKindTable)) {
            while (resultSetForQueryForTableName.next()) {
                if (!resultSetForQueryForTableName.isNull("assetTableName")) {
                    String tableName = resultSetForQueryForTableName.getString("assetTableName");
                    ResultSet resultSetForKindTable = runConfigQuery(tableName, dbClient);
                    fillInLists(resultSetForKindTable, columnDisplays, columnNames,
                            columnNamesForKindQuery,
                            columnTypes, columnsForUsing, tableName, false);
                    String columns = String.join(", ", columnNamesForKindQuery);
                    String usingString = String.join(", ", columnsForUsing);
                    statementString = String.format("SELECT %s FROM Main_Assets JOIN %s USING " +
                                    "(%s) WHERE workspaceId = @workspaceId ORDER By %s"
                            , columns, tableName, usingString, columnNames.get(0));
                }
            }
        }
        TableQueryObject tableQueryObject = new TableQueryObject(columnDisplays, columnNames,
                columnTypes, statementString);
        return tableQueryObject;
    }

    /**
     * This is used to create TableQueryObject dynamically for all cases other than kind
     *
     * @param dbClient - A client for connection to the DB
     * @param filters  - The types of filters we want to use
     * @return A TableQueryObject for using when running query
     */
    public TableQueryObject createTableQueryObject(DatabaseClient dbClient, List<String> filters) {

        List<String> columnDisplays = new ArrayList<>();
        ;
        List<String> columnNames = new ArrayList<>();
        List<String> columnTypes = new ArrayList<>();
        ResultSet resultSetForAllAssets = runConfigQuery("forAllAssets", dbClient);

        while (resultSetForAllAssets.next()) {
            columnDisplays.add(resultSetForAllAssets.getString("columnDisplayName"));
            columnNames.add(resultSetForAllAssets.getString("columnName"));
            columnTypes.add(resultSetForAllAssets.getString("columnType"));
        }

        ResultSet resultSetForMainAssets = runConfigQuery("Main_Assets", dbClient);
        while (resultSetForMainAssets.next()) {
            columnDisplays.add(resultSetForMainAssets.getString("columnDisplayName"));
            columnNames.add(resultSetForMainAssets.getString("columnName"));
            columnTypes.add(resultSetForMainAssets.getString("columnType"));
        }
        String columns = String.join(", ", columnNames);
        String statementString;
        if (filters.isEmpty()) {
            statementString = String.format("SELECT %s FROM Main_Assets WHERE workspaceId = " +
                            "@workspaceId ORDER By %s", columns,
                    columnNames.get(0));
        } else {
            List<String> queryFilters = new ArrayList<>();
            for (String filter : filters) {
                String oneFilter = String.format("%s = @%s", filter, filter);
                queryFilters.add(oneFilter);
            }
            String workspaceIdFilter = String.format("%s", "workspaceId = @workspaceId");
            queryFilters.add(workspaceIdFilter);
            String where = String.join(" AND ", queryFilters);
            statementString = String.format("SELECT %s FROM Main_Assets WHERE %s ORDER By %s",
                    columns, where, columnNames.get(0));
        }

        TableQueryObject tableQueryObject = new TableQueryObject(columnDisplays, columnNames,
                columnTypes, statementString);
        return tableQueryObject;
    }

    /**
     * This builds and executes the query with all the different filters
     *
     * @param dbClient    - A client for connection to the DB
     * @param location    - The wanted location from the user
     * @param status      - The wanted status from the user
     * @param kind        - The wanted kind from the user
     * @param workspaceId - The chosen workspace from the user
     * @return A ResultListObject witch holds a list of display names for each column and a list
     * of lists where each row holds information of one asset in strings (to use
     * in template)
     */
    public ResultListObject getAllAssets(DatabaseClient dbClient, String location, String status,
                                         String kind, String workspaceId) {
        List<String> filters = new ArrayList<>();
        if (!status.equals("all")) {
            filters.add("status");
        }
        if (!location.equals("all")) {
            filters.add("location");
        }
        if (!kind.equals("all")) {
            filters.add("kind");
        }
        TableQueryObject tableQueryObject = createTableQueryObject(dbClient, filters);
        Statement statement =
                Statement.newBuilder(tableQueryObject.Query).bind("location").to(location).bind(
                        "status").to(status).bind("kind").to(kind).bind("workspaceId").to(workspaceId).build();
        List<List<String>> results = executeQueryAndReturnList(statement, dbClient,
                tableQueryObject.columnNames, tableQueryObject.columnTypes);
        ResultListObject resultList = new ResultListObject(tableQueryObject.columnDisplays,
                results);
        return resultList;
    }

    /**
     * This builds and executes the query with kind (type of asset) filter
     *
     * @param dbClient    - A client for connection to the DB
     * @param kind        - The wanted status from the user
     * @param workspaceId - The chosen workspace from the user
     * @return A ResultListObject witch holds a list of display names for each column and a list
     * of lists where each row holds information of one asset in strings (to use
     * in template)
     */
    public ResultListObject getAssetsByKind(DatabaseClient dbClient, String kind,
                                            String workspaceId) {
        TableQueryObject tableQueryObject = createTableQueryObjectForKind(dbClient, kind);
        Statement statement =
                Statement.newBuilder(tableQueryObject.Query).bind("workspaceId").to(workspaceId).build();
        List<List<String>> results = executeQueryAndReturnList(statement, dbClient,
                tableQueryObject.columnNames, tableQueryObject.columnTypes);
        ResultListObject resultList = new ResultListObject(tableQueryObject.columnDisplays,
                results);
        return resultList;
    }


}

