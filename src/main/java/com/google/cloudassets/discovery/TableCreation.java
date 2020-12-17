package com.google.cloudassets.discovery;

import com.google.cloud.spanner.ResultSet;
import com.google.cloudassets.discovery.AssetDiscoveryExceptions.ConfigTableException;
import com.google.cloudassets.discovery.AssetDiscoveryExceptions.TableCreationException;

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

    private String tableName;
    private StringBuilder createStatement;

    /**
     * This function constructs a TableCreation object with the given table name.
     * @param name - a string representing the table for which to create the statement.
     */
    public TableCreation(String name) {
        this.tableName = name;
        this.createStatement = new StringBuilder();
    }

    /**
     * This function returns a DDL create table statement for the given table name.
     * @return a string of the DDL create table statement.
     * @throws TableCreationException
     */
    public String getCreateTableStatement() throws TableCreationException {
        // No need to reconstruct the create statement if this function was already called once
        if (this.createStatement.length() == 0) {
            // Add CREATE TABLE statement and common columns
            addCommonColumnsStatement();

            // Add specific table columns
            ResultSet tableConfig = executeStringQuery(GET_TABLES_CONFIG_QUERY + " WHERE assetTableName = '"
                    + this.tableName + "'");
            addColumnsStatement(tableConfig);

            // Add primary keys
            addCommonPrimaryKeysStatement();

            try {
                // Add interleaved statement for all tables except for the main asset table
                if (!this.tableName.equals(AssetKind.getMainTableName())) {
                    addInterleavedStatement();
                }
            } catch (ConfigTableException exception) {
                String errorMsg = "Could not construct the DDL create table statement for " + tableName;
                throw new TableCreationException(errorMsg, exception);
            }
        }

        return this.createStatement.toString();
    }

    /*
    This function appends a string of the beginning of the DDL create table statement for the given
    table name with the columns that are common for all of the asset tables.
     */
    private void addCommonColumnsStatement() {
        this.createStatement.append("CREATE TABLE " + this.tableName + " (");

        ResultSet commonConfig = executeStringQuery(GET_TABLES_CONFIG_QUERY + " WHERE assetTableName = 'forAllAssets'");
        addColumnsStatement(commonConfig);
    }

    /*
    This function appends a string representing the columns part of the DDL create table statement
    to the createStatement variable.
     */
    private void addColumnsStatement(ResultSet tableConfig) {
        while (tableConfig.next()) {
            this.createStatement.append(tableConfig.getString("columnName"));
            this.createStatement.append(" ");
            this.createStatement.append(tableConfig.getString("columnType"));
            if (tableConfig.getBoolean("isNotNull")) {
                this.createStatement.append(" NOT NULL");
            }
            if (tableConfig.getBoolean("allowCommitTimestamp")) {
                this.createStatement.append(" OPTIONS (allow_commit_timestamp=true)");
            }
            this.createStatement.append(", ");
        }
    }

    /*
    This function appends a string of the primary keys part of the DDL create table statement which is
    common to all of the asset tables - should be used after the statement of last column wanted for
    a given table.
     */
    private void addCommonPrimaryKeysStatement() {
        ResultSet tableConfig = executeStringQuery(GET_COMMON_PRIMARY_KEYS_QUERY);

        this.createStatement.append(") PRIMARY KEY (");

        while (tableConfig.next()) {
            this.createStatement.append(tableConfig.getString("columnName"));
            this.createStatement.append(", ");
        }
        // Delete the last comma from the createStatement variable
        if (this.createStatement.length() > 0) {
            this.createStatement.delete(this.createStatement.length() - 2, this.createStatement.length());
        }

        this.createStatement.append(")");
    }

    /*
    This function appends a string of the interleave statements part of the DDL create table statement -
    should be used after the primary keys statement.
    Throws a ConfigTableException if the main table is not properly configured in the configuration
    table.
     */
    private void addInterleavedStatement() throws ConfigTableException {
        this.createStatement.append(", INTERLEAVE IN PARENT " + AssetKind.getMainTableName() + " ON DELETE CASCADE");
    }
}
