package resourceDisplay;

import com.google.cloud.spanner.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class does all DB access
 */
public class AssetsRepository {
    /**
     * This is just for testing the function that returns assets by type
     * @return a list of TestAsset objects
     */
    public List<TestAsset> executeAllAssetsQuery(Statement statement, DatabaseClient dbClient){
       List<TestAsset> allAssets = new ArrayList<>();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                TestAsset asset = new TestAsset();
                if (!resultSet.isNull("assetName")){
                    asset.setName(resultSet.getString("assetName"));
                }
                if (!resultSet.isNull("kind")){
                    asset.setKind(resultSet.getString("kind"));
                }
                if (!resultSet.isNull("location")){
                    asset.setZone(resultSet.getString("location"));
                }
                if (!resultSet.isNull("creationTime")){
                    asset.setCreationTime(resultSet.getTimestamp("creationTime"));
                }
                if (!resultSet.isNull("status")){
                    asset.setStatus(resultSet.getString("status"));
                }
                allAssets.add(asset);
            }
            return allAssets;
        }
    }
    /**
     * This is used to return all the distinct statuses in a project/workspace (at the moment workspaceID)
     * @param dbClient - A client for connection to the DB
     * @param workspaceId - The ID of the wanted workspace
     * @return a list of strings holding the different statuses in a project/workspace
     */
    public List<String> getStatusList(DatabaseClient dbClient, String workspaceId) {
        List<String> statusList = new ArrayList<>();
        Statement statement =
                Statement.newBuilder(
                        "SELECT DISTINCT status FROM Main_Assets "
                                + "WHERE workspaceId = @workspaceId")
                        .bind("workspaceId")
                        .to(workspaceId)
                        .build();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                if (!resultSet.isNull("status")) {
                    statusList.add(resultSet.getString("status"));
                }
            }
        }
        return statusList;
    }
    /**
     * This is used to return all the distinct locations in a project/workspace (at the moment workspaceID)
     * @param dbClient - A client for connection to the DB
     * @param workspaceId - The ID of the wanted workspace
     * @return a list of strings holding the different locations in a project/workspace
     */
    public List<String> getLocationList(DatabaseClient dbClient, String workspaceId) {
        List<String> locationList = new ArrayList<>();
        Statement statement =
                Statement.newBuilder(
                        "SELECT DISTINCT location FROM Main_Assets "
                        + "WHERE workspaceId = @workspaceId")
                        .bind("workspaceId")
                .to(workspaceId)
                .build();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                if (!resultSet.isNull("location")) {
                    locationList.add(resultSet.getString("location"));
                }
            }
        }
        return locationList;
    }
    /**
     * This is used to execute a query that was built dynamically and column types are unknown and return a list of data
     * @param statement - A statement that holds the query
     * @param dbClient - A client for connection to the DB
     * @param columnNames - A list of column names to use for getting real types
     * @param columnTypes - A list of column types to use with Arrays
     * @return a list os lists where each row holds information of one asset in strings (to use in template)
     */
    public List<List<String>> executeQueryAndReturnList(Statement statement, DatabaseClient dbClient, List<String> columnNames,  List<String> columnTypes){
        List<List<String>> allAssets = new ArrayList<>();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for(int i = 0; i < columnNames.size(); i++) {
                    if (!resultSet.isNull(columnNames.get(i))){
                        Type type = resultSet.getColumnType(columnNames.get(i));
                        switch (type.getCode()) {
                            case ARRAY :
                                //todo
                                // maybe need specific array type from table (or maybe we can get from type field)
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
                                //todo?
                                break;
                            case TIMESTAMP:
                                row.add(String.valueOf(resultSet.getTimestamp(columnNames.get(i))));
                                break;
                        }
                    }
                    else{
                        row.add("");
                    }
                }
                allAssets.add(row);
            }
            return allAssets;
        }
    }
    /**
     * This is used to create query dynamically
     * @param dbClient - A client for connection to the DB
     * @param tableName - The table we want to query
     * @param columnDisplays - A list of strings to display as column headers to fill
     * @param columnNames - A list of column names to fill
     * @param columnTypes - A list of column types to fill
     * @param filterType - The type of filter the query needs to execute
     * @return statement that holds the query to execute
     */
    public String createQueryAndDisplayNameList(DatabaseClient dbClient, String tableName, List<String> columnDisplays, List<String> columnNames, List<String> columnTypes, String filterType){
        /* this part we do for all tables*/
        Statement statement1 =
                Statement.newBuilder(
                        "SELECT columnName, columnDisplayName, columnType FROM Asset_Tables_Config "
                                + "WHERE assetTableName = 'forAllAssets' "
                                + "and toDisplay = True")
                        .build();
        String strResult = "";
        boolean isFirst = true;
        try (ResultSet resultSet1 = dbClient.singleUse().executeQuery(statement1)) {
            while (resultSet1.next()) {
                String str = resultSet1.getString("columnName");
                String display = resultSet1.getString("columnDisplayName");
                String type = resultSet1.getString("columnType");
                if(isFirst) {
                    isFirst = false;
                }
                else{
                    strResult += ", ";
                }
                strResult += str;
                columnDisplays.add(display);
                columnNames.add(str);
                columnTypes.add(type);
            }
        }
        /* this part gets columns for the specific table*/
        Statement statement2 =
                Statement.newBuilder(
                        "SELECT columnName, columnDisplayName, columnType FROM Asset_Tables_Config "
                                + "WHERE assetTableName = @tableName "
                                + "and toDisplay = True")
                        .bind("tableName")
                        .to(tableName)
                        .build();
        try (ResultSet resultSet2 = dbClient.singleUse().executeQuery(statement2)) {
            while (resultSet2.next()) {
                String str = resultSet2.getString("columnName");
                String display = resultSet2.getString("columnDisplayName");
                String type = resultSet2.getString("columnType");
                if(isFirst) {
                    isFirst = false;
                }
                else{
                    strResult += ", ";
                }
                strResult += str;
                columnDisplays.add(display);
                columnNames.add(str);
                columnTypes.add(type);
            }
        }
        String statementString = "SELECT ";
        statementString += strResult;
        statementString += " FROM ";
        statementString += tableName;
        if(filterType == "location") {
            statementString += " WHERE location = @location";
        }
        else if(filterType == "status") {
            statementString += " WHERE status = @status";
        }
        else if(filterType == "kind") {
            //todo maybe need more specific things for kind!!!!
            statementString += " WHERE kind = @kind";
        }
        return statementString;
    }
    /**
     * This builds and executes the query for all assets
     * @param dbClient - A client for connection to the DB
     * @param columnDisplays - A list of strings to display as column headers to fill
     * @return a list os lists where each row holds information of one asset in strings (to use in template)
     */
    public List<List<String>> getAllAssets(DatabaseClient dbClient, List<String> columnDisplays) {
        List<String> columnNames= new ArrayList<>();
        List<String> columnTypes= new ArrayList<>();
        Statement statement;
        String statementString = createQueryAndDisplayNameList(dbClient, "Main_Assets", columnDisplays, columnNames, columnTypes, "none");
        statement = Statement.newBuilder(statementString).build();
        return executeQueryAndReturnList(statement, dbClient, columnNames, columnTypes);
    }
    /**
     * This builds and executes the query with status filter
     * @param dbClient - A client for connection to the DB
     * @param columnDisplays - A list of strings to display as column headers to fill
     * @param status - The wanted status from the user
     * @return a list os lists where each row holds information of one asset in strings (to use in template)
     */
    public List<List<String>> getAssetsByStatus(DatabaseClient dbClient, List<String> columnDisplays, String status) {

        /*List<TestAsset> allAssets = new ArrayList<>();
        Statement statement =
                Statement.newBuilder(
                        "SELECT assetName, kind, location, creationTime, status "
                                + "FROM Main_Assets "
                                + "WHERE status = @status")
                        .bind("status")
                        .to(status)
                        .build();
        return executeAllAssetsQuery(statement, dbClient);*/
        List<String> columnNames= new ArrayList<>();
        List<String> columnTypes= new ArrayList<>();
        Statement statement;
        String statementString = createQueryAndDisplayNameList(dbClient, "Main_Assets", columnDisplays, columnNames, columnTypes, "status");
        statement = Statement.newBuilder(statementString).bind("status").to(status).build();
        return executeQueryAndReturnList(statement, dbClient, columnNames, columnTypes);
    }
    /**
     * This builds and executes the query with kind (type of asset) filter
     * @param dbClient - A client for connection to the DB
     * @param columnDisplays - A list of strings to display as column headers to fill
     * @param kind - The wanted status from the user
     * @return a list os lists where each row holds information of one asset in strings (to use in template)
     */
    public List<TestAsset> getAssetByKind(DatabaseClient dbClient, List<String> columnDisplays, String kind) {
        // not done
        List<TestAsset> allAssetsOfType = new ArrayList<>();
        Statement statement =
                Statement.newBuilder(
                        "SELECT assetName, kind, location , creationTime, status "
                                + "FROM Main_Assets "
                                + "WHERE kind = @kind")
                        .bind("kind")
                        .to(kind)
                        .build();
        return executeAllAssetsQuery(statement, dbClient);
    }
    /**
     * This builds and executes the query with status filter
     * @param dbClient - A client for connection to the DB
     * @param columnDisplays - A list of strings to display as column headers to fill
     * @param location - The wanted location from the user
     * @return a list os lists where each row holds information of one asset in strings (to use in template)
     */
    public List<List<String>> getAssetsByLocation(DatabaseClient dbClient, List<String> columnDisplays, String location) {
        /*List<TestAsset> allAssetsOfType = new ArrayList<>();
        Statement statement =
                Statement.newBuilder(
                        "SELECT assetName, kind, location , creationTime, status "
                                + "FROM Main_Assets "
                                + "WHERE location = @location")
                        .bind("location")
                        .to(location)
                        .build();
        return executeAllAssetsQuery(statement, dbClient);*/
        List<String> columnNames= new ArrayList<>();
        List<String> columnTypes= new ArrayList<>();
        Statement statement;
        String statementString = createQueryAndDisplayNameList(dbClient, "Main_Assets", columnDisplays, columnNames, columnTypes, "location");
        statement = Statement.newBuilder(statementString).bind("location").to(location).build();
        return executeQueryAndReturnList(statement, dbClient, columnNames, columnTypes);
    }

}

