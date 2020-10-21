package com.google.cloudassets.discovery;

import com.google.cloud.spanner.ResultSet;
import static com.google.cloudassets.discovery.Main.executeStringQuery;

/**
 * This class generates a DDL create table statement for a given table name (based on the
 * Asset_Tables_Config table).
 */
public class TableCreation {
    private static final String GET_TABLES_CONFIG_QUERY = "SELECT columnName, columnType, "
                                                        + "isNotNull, isPrimaryKey, "
                                                        + "allowCommitTimestamp "
                                                        + "FROM Asset_Tables_Config";
    // The order of the primary keys in the create table statement matters
    private static final String GET_COMMON_PRIMARY_KEYS_QUERY = "SELECT columnName "
                                                        + "FROM Asset_Tables_Config "
                                                        + "WHERE assetTableName = 'forAllAssets' "
                                                        + "and isPrimaryKey = True "
                                                        + "ORDER BY primaryKeyIndex";

    private static StringBuilder createStatement;

    /**
     * This function returns a DDL create table statement for the given table name.
     * @param tableName - a string representing the table for which to create the statement.
     * @return a string of the DDL create table statement.
     */
    public static String getCreateTableStatement(String tableName) {
        createStatement = new StringBuilder();
        // Add CREATE TABLE statement and common columns
        addCommonColumnsStatement(tableName);

        // Add specific table columns
        ResultSet tableConfig = executeStringQuery(GET_TABLES_CONFIG_QUERY + " WHERE assetTableName = '" + tableName + "'");
        addColumnsStatement(tableConfig);

        // Add primary keys
        addCommonPrimaryKeysStatement();

        // Add interleaved statement for all tables except for the main asset table
        if (!tableName.equals(AssetKind.getMainTableName())) {
            addInterleavedStatement();
        }

        return createStatement.toString();
    }

    /*
    This function appends a string of the beginning of the DDL create table statement for the given
    table name with the columns that are common for all of the asset tables.
     */
    private static void addCommonColumnsStatement(String tableName) {
        createStatement.append("CREATE TABLE " + tableName + " (");

        ResultSet commonConfig = executeStringQuery(GET_TABLES_CONFIG_QUERY + " WHERE assetTableName = 'forAllAssets'");
        addColumnsStatement(commonConfig);
    }

    /*
    This function appends a string representing the columns part of the DDL create table statement
    to the createStatement variable.
     */
    private static void addColumnsStatement(ResultSet tableConfig) {
        while (tableConfig.next()) {
            createStatement.append(tableConfig.getString("columnName"));
            createStatement.append(" ");
            createStatement.append(tableConfig.getString("columnType"));
            if (tableConfig.getBoolean("isNotNull")) {
                createStatement.append(" NOT NULL");
            }
            if (tableConfig.getBoolean("allowCommitTimestamp")) {
                createStatement.append(" OPTIONS (allow_commit_timestamp=true)");
            }
            createStatement.append(", ");
        }
    }

    /*
    This function appends a string of the primary keys part of the DDL create table statement which is
    common to all of the asset tables - should be used after the statement of last column wanted for
    a given table.
     */
    private static void addCommonPrimaryKeysStatement() {
        ResultSet tableConfig = executeStringQuery(GET_COMMON_PRIMARY_KEYS_QUERY);

        createStatement.append(") PRIMARY KEY (");

        while (tableConfig.next()) {
            createStatement.append(tableConfig.getString("columnName"));
            createStatement.append(", ");
        }
        // Delete the last comma from the createStatement variable
        if (createStatement.length() > 0) {
            createStatement.delete(createStatement.length() - 2, createStatement.length());
        }

        createStatement.append(")");
    }

    /*
    This function appends a string of the interleave statements part of the DDL create table statement -
    should be used after the primary keys statement.
     */
    private static void addInterleavedStatement() {
        createStatement.append(", INTERLEAVE IN PARENT " + AssetKind.getMainTableName() + " ON DELETE CASCADE");
    }
}
