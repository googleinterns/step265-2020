package com.google.cloudassets.discovery;

/**
 * This enum class contains all of the tables that should be created for this project together with
 * their CREATE TABLE queries.
 */
public enum AssetTable {
    // MAIN_TABLE must be first as all other tables have an interleaved relationship with this table
    MAIN_TABLE("Main_Assets", getCommonColumnsString("Main_Assets") +
            " assetId STRING(MAX)," +
            " assetType STRING(MAX)," +
            " creationTime TIMESTAMP," +
            " status STRING(MAX)," +
            " location STRING(MAX)," +
            getPrimaryKeysStatementString()),

    BUCKET_STORAGE_TABLE("Bucket_Storage_Assets", getCommonColumnsString("Bucket_Storage_Assets") +
            " storageClass STRING(MAX)," +
            " updatedTime TIMESTAMP," +
            getInterleavedString()),

    DISK_COMPUTE_TABLE("Disk_Compute_Assets", getCommonColumnsString("Disk_Compute_Assets") +
            " diskSizeGb INT64," +
            " updatedTime TIMESTAMP," +
            " licenses ARRAY<STRING(MAX)>," +
            getInterleavedString()),

    INSTANCE_CLOUD_SQL_TABLE("Instance_Cloud_Sql_Assets",   getCommonColumnsString("Instance_Cloud_Sql_Assets") +
            " etag STRING(MAX)," +
            " diskSizeGb INT64," +
            " backupEnabled BOOL," +
            " replicationType STRING(MAX)," +
            " activationPolicy STRING(MAX)," +
            getInterleavedString()),

    INSTANCE_COMPUTE_TABLE("Instance_Compute_Assets", getCommonColumnsString("Instance_Compute_Assets") +
            " description STRING(MAX)," +
            " canIpForward BOOL," +
            " cpuPlatform STRING(MAX)," +
            getInterleavedString()),

    SUBSCRIPTION_PUB_SUB_TABLE("Subscription_Pub_Sub_Assets", getCommonColumnsString("Subscription_Pub_Sub_Assets") +
            " topic STRING(MAX)," +
            " ttl STRING(MAX)," +
            getInterleavedString()),

    TOPIC_PUB_SUB_TABLE("Topic_Pub_Sub_Assets", getCommonColumnsString("Topic_Pub_Sub_Assets") +
            " allowedPersistenceRegions ARRAY<STRING(MAX)>," +
    getInterleavedString());

    private final String tableName;
    private final String tableCreateQuery;

    /*
    This private constructor initialized the fields for the given enum.
     */
    private AssetTable(String name, String query) {
        this.tableName = name;
        this.tableCreateQuery = query;
    }

    /**
     * @return a string representing the table name associated with the given asset enum.
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * @return a string representing the CREATE TABLE query for the asset table associated with the
     * given asset enum.
     */
    public String getTableCreateQuery() {
        return this.tableCreateQuery;
    }

    /*
    This function returns a string of the beginning of the CREATE TABLE query for the given table
    name with the columns that are common for all of the asset tables.
     */
    private static String getCommonColumnsString(String tblName) {
        return "CREATE TABLE " + tblName + " (" +
                " workspaceId STRING(MAX) NOT NULL," +
                " projectId STRING(MAX) NOT NULL," +
                " kind STRING(MAX) NOT NULL," +
                " assetName STRING(MAX) NOT NULL," +
                " rowLastUpdateTime TIMESTAMP NOT NULL OPTIONS (allow_commit_timestamp=true), ";
    }

    /*
    This function returns a string of PRIMARY KEY statement part of the CREATE TABLE query - should
    be used after the statement of last column wanted for the asset table.
     */
    private static String getPrimaryKeysStatementString() {
        return ") PRIMARY KEY (workspaceId, projectId, assetName, kind)";
    }

    /*
    This function returns a string of PRIMARY KEY & INTERLEAVE statements part of the CREATE TABLE
    query - should be used after the statement of last column wanted for the asset table.
     */
    private static String getInterleavedString() {
        return getPrimaryKeysStatementString() + ", INTERLEAVE IN PARENT Main_Assets ON DELETE CASCADE";
    }
}
