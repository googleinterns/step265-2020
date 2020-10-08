package resourceDisplay;

import com.google.cloud.spanner.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class does all DB access
 */
public class AssetsRepository {

    public List<String> getFilterList(DatabaseClient dbClient, String workspaceId, String filterType) {
        List<String> filterList = new ArrayList<>();
        String statementString = "SELECT DISTINCT ";
        statementString += filterType;
        statementString += " FROM Main_Assets WHERE workspaceId = @workspaceId";
        Statement statement = Statement.newBuilder(statementString)
                .bind("workspaceId")
                .to(workspaceId)
                .build();
        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                if (filterType.equals("status")){
                    if (!resultSet.isNull("status")) {
                        filterList.add(resultSet.getString("status"));
                    }
                }
                else if (filterType.equals("location")){
                    if (!resultSet.isNull("location")) {
                        filterList.add(resultSet.getString("location"));
                    }
                }
                else { // the filterType is kind
                    if (!resultSet.isNull("kind")) {
                        filterList.add(resultSet.getString("kind"));
                    }
                }
            }
        }
        return filterList;
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
     * This is used specifically to build a query for filtering by Kind because we need to use a join
     * @param dbClient - A client for connection to the DB
     * @param columnDisplays - A list of strings to display as column headers to fill
     * @param columnNames - A list of column names to fill
     * @param columnTypes - A list of column types to fill
     * @param kind - Holds the kind of asset we want to filter by
     * @param strResult - Holds the query built so far by the function that called this function
     * @return statement that holds the query to execute
     */
    public String buildQueryForKind(DatabaseClient dbClient, List<String> columnDisplays, List<String> columnNames, List<String> columnTypes, String kind, String strResult){
        String statementString = "";
        Statement statement1 =
                Statement.newBuilder(
                        "SELECT columnName, columnDisplayName, columnType FROM Asset_Tables_Config "
                                + "WHERE assetTableName = 'Main_Assets' "
                                + "and toDisplay = True")
                        .build();
        try (ResultSet resultSet1 = dbClient.singleUse().executeQuery(statement1)) {
            while (resultSet1.next()) {
                String str = resultSet1.getString("columnName");
                String display = resultSet1.getString("columnDisplayName");
                String type = resultSet1.getString("columnType");
                strResult += ", ";
                strResult += "Main_Assets.";
                strResult += str;
                columnDisplays.add(display);
                columnNames.add(str);
                columnTypes.add(type);
            }
        }
        Statement statement2 =
                Statement.newBuilder(
                        "SELECT DISTINCT assetTableName FROM Asset_Tables_Config "
                                + "WHERE assetKind = @assetKind")
                        .bind("assetKind")
                        .to(kind)
                        .build();
        try (ResultSet resultSet2 = dbClient.singleUse().executeQuery(statement2)) {
            while (resultSet2.next()) {
                if (!resultSet2.isNull("assetTableName")) {
                    String tableName = resultSet2.getString("assetTableName");
                    Statement statement3 =
                            Statement.newBuilder(
                                    "SELECT columnName, columnDisplayName, columnType FROM Asset_Tables_Config "
                                            + "WHERE assetTableName = @tableName "
                                            + "and toDisplay = True")
                                    .bind("tableName")
                                    .to(tableName)
                                    .build();
                    try (ResultSet resultSet3 = dbClient.singleUse().executeQuery(statement3)) {
                        while (resultSet3.next()) {
                            String str = resultSet3.getString("columnName");
                            String display = resultSet3.getString("columnDisplayName");
                            String type = resultSet3.getString("columnType");
                            strResult += ", ";
                            strResult += tableName;
                            strResult += ".";
                            strResult += str;
                            columnDisplays.add(display);
                            columnNames.add(str);
                            columnTypes.add(type);
                        }
                    }
                    statementString = "SELECT ";
                    statementString += strResult;
                    statementString += " FROM Main_Assets JOIN ";
                    statementString += tableName;
                }
            }
        }
        return statementString;
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
    public String createQueryAndDisplayNameList(DatabaseClient dbClient, String tableName, List<String> columnDisplays, List<String> columnNames, List<String> columnTypes, String filterType, String kind) {
        /* this part we do for all tables*/
        Statement statement1 =
                Statement.newBuilder(
                        "SELECT columnName, columnDisplayName, columnType FROM Asset_Tables_Config "
                                + "WHERE assetTableName = 'forAllAssets' "
                                + "and toDisplay = True")
                        .build();
        String strResult = "";
        boolean isFirst = true;
        boolean isFirstUsingForKind = true;
        String using = " USING (";
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
                if(filterType.equals("kind")) {
                    strResult += "Main_Assets.";
                    if(isFirstUsingForKind) {
                        isFirstUsingForKind = false;
                    }
                    else{
                        using += ", ";
                    }
                    using += str;
                }
                strResult += str;
                columnDisplays.add(display);
                columnNames.add(str);
                columnTypes.add(type);
            }
        }
        /* this part gets columns for the specific table*/
        String statementString;
        if (filterType.equals("kind")) {
            statementString = buildQueryForKind(dbClient, columnDisplays, columnNames, columnTypes, kind, strResult);
            statementString += using;
            statementString += ")";
        }
        else {
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
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        strResult += ", ";
                    }
                    strResult += str;
                    columnDisplays.add(display);
                    columnNames.add(str);
                    columnTypes.add(type);
                }
            }
            statementString = "SELECT ";
            statementString += strResult;
            statementString += " FROM ";
            statementString += tableName;
            if (filterType.equals("location")) {
                statementString += " WHERE location = @location";
            } else if (filterType.equals("status")) {
                statementString += " WHERE status = @status";
            }
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
        String statementString = createQueryAndDisplayNameList(dbClient, "Main_Assets", columnDisplays, columnNames, columnTypes, "none", "all");
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
        List<String> columnNames= new ArrayList<>();
        List<String> columnTypes= new ArrayList<>();
        Statement statement;
        String statementString = createQueryAndDisplayNameList(dbClient, "Main_Assets", columnDisplays, columnNames, columnTypes, "status", "all");
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
    public List<List<String>>  getAssetByKind(DatabaseClient dbClient, List<String> columnDisplays, String kind) {
        List<String> columnNames= new ArrayList<>();
        List<String> columnTypes= new ArrayList<>();
        Statement statement;
        String statementString = createQueryAndDisplayNameList(dbClient, "Main_Assets", columnDisplays, columnNames, columnTypes, "kind", kind);
        statement = Statement.newBuilder(statementString).build();
        return executeQueryAndReturnList(statement, dbClient, columnNames, columnTypes);
    }

    /**
     * This builds and executes the query with status filter
     * @param dbClient - A client for connection to the DB
     * @param columnDisplays - A list of strings to display as column headers to fill
     * @param location - The wanted location from the user
     * @return a list os lists where each row holds information of one asset in strings (to use in template)
     */
    public List<List<String>> getAssetsByLocation(DatabaseClient dbClient, List<String> columnDisplays, String location) {
        List<String> columnNames= new ArrayList<>();
        List<String> columnTypes= new ArrayList<>();
        Statement statement;
        String statementString = createQueryAndDisplayNameList(dbClient, "Main_Assets", columnDisplays, columnNames, columnTypes, "location", "all");
        statement = Statement.newBuilder(statementString).bind("location").to(location).build();
        return executeQueryAndReturnList(statement, dbClient, columnNames, columnTypes);
    }

}

