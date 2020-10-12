package com.google.cloudassets.discovery;

import com.google.cloud.spanner.ResultSet;
import static com.google.cloudassets.discovery.Main.executeStringQuery;

/**
 * This class generates an sql create table query for a given table name (based on the
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

    private static StringBuilder createQuery;

    /**
     * This function generates an SQL create table query for the given table name.
     * @param tableName - a string representing the table for which to create the statement.
     * @return a string of the SQL query.
     */
    public static String getCreateTableQuery(String tableName) {
        createQuery = new StringBuilder();
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

        return createQuery.toString();
    }

    /*
    This function appends a string of the beginning of the create table query for the given table
    name with the columns that are common for all of the asset tables.
     */
    private static void addCommonColumnsStatement(String tableName) {
        createQuery.append("CREATE TABLE " + tableName + " (");

        ResultSet commonConfig = executeStringQuery(GET_TABLES_CONFIG_QUERY + " WHERE assetTableName = 'forAllAssets'");
        addColumnsStatement(commonConfig);
    }

    /*
    This function appends a string representing the columns part of the create table query to the
    createQuery variable.
     */
    private static void addColumnsStatement(ResultSet tableConfig) {
        while (tableConfig.next()) {
            createQuery.append(tableConfig.getString("columnName"));
            createQuery.append(" ");
            createQuery.append(tableConfig.getString("columnType"));
            if (tableConfig.getBoolean("isNotNull")) {
                createQuery.append(" NOT NULL");
            }
            if (tableConfig.getBoolean("allowCommitTimestamp")) {
                createQuery.append(" OPTIONS (allow_commit_timestamp=true)");
            }
            createQuery.append(", ");
        }
    }

    /*
    This function appends a string of the primary keys part of the create table query which is
    common to all of the asset tables - should be used after the statement of last column wanted for
    a given table.
     */
    private static void addCommonPrimaryKeysStatement() {
        ResultSet tableConfig = executeStringQuery(GET_COMMON_PRIMARY_KEYS_QUERY);

        createQuery.append(") PRIMARY KEY (");

        while (tableConfig.next()) {
            createQuery.append(tableConfig.getString("columnName"));
            createQuery.append(", ");
        }
        // Delete the last comma from the createQuery variable
        if (createQuery.length() > 0) {
            createQuery.delete(createQuery.length() - 2, createQuery.length());
        }

        createQuery.append(")");
    }

    /*
    This function appends a string of the interleave statements part of the create table query -
    should be used after the primary keys statement.
     */
    private static void addInterleavedStatement() {
        createQuery.append(", INTERLEAVE IN PARENT " + AssetKind.getMainTableName() + " ON DELETE CASCADE");
    }
}
