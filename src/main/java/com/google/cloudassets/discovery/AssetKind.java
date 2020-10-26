package com.google.cloudassets.discovery;

import com.google.cloud.spanner.ResultSet;
import com.google.cloudassets.discovery.exceptions.ConfigTableException;

import static com.google.cloudassets.discovery.Main.executeStringQuery;

/**
 This enum class maps the kind string for each asset and provides its relevant asset table name in
 our spanner DB.
 */
public enum AssetKind {
    BUCKET_STORAGE_ASSET("storage#bucket"),
    DISK_COMPUTE_ASSET("compute#disk"),
    INSTANCE_CLOUD_SQL_ASSET("sql#instance"),
    INSTANCE_COMPUTE_ASSET("compute#instance"),
    SUBSCRIPTION_PUB_SUB_ASSET("pubsub#subscription"),
    TOPIC_PUB_SUB_ASSET("pubsub#topic"),
    INSTANCE_SPANNER_ASSET("spanner#instance");

    private final String kindString;

    /*
    This private constructor initialized the fields for the given enum.
     */
    private AssetKind(String kind) {
        this.kindString = kind;
    }

    /**
     * @return a string representing the kind of this asset.
     */
    @Override
    public String toString() {
        return this.kindString;
    }

    /**
     * @return a string representing the table name of a given asset kind.
     * @throws ConfigTableException if the asset kind table is not properly configured in the
     * configuration table.
     */
    public String getAssetTableName() throws ConfigTableException {
        String errorMsg = " was configured as the asset table in Asset_Tables_Config for the '"
                + this.kindString + "' asset kind. Please make sure that exactly one table is configured"
                + " for each asset kind.";
        String queryStr = "SELECT DISTINCT assetTableName FROM Asset_Tables_Config WHERE assetKind = '"
                + this.kindString + "'";
        return getTableName(queryStr, errorMsg);
    }

    /**
     * @return a string representing the main asset table name.
     * @throws ConfigTableException if the main table is not properly configured in the configuration
     * table.
     */
    public static String getMainTableName() throws ConfigTableException {
        String errorMsg = " was configured as the main table in Asset_Tables_Config. "
                + "Please make sure that exactly one table has the 'isMainTable' flag on.";
        String queryStr = "SELECT DISTINCT assetTableName FROM Asset_Tables_Config WHERE isMainTable = True";
        return getTableName(queryStr, errorMsg);
    }

    /*
    This function returns a table name returned from the provided queryStr. If there is not exactly
    one table name returned from the provided query it means that the Asset_Tables_Config is not
    configured properly and therefore unexpected behaviors may arise. In this case a
    ConfigTableException is thrown.
     */
    private static String getTableName(String queryStr, String errorMsg) throws ConfigTableException {
        ResultSet resultSet = executeStringQuery(queryStr);
        String tableName = null;
        if (resultSet.next()) {
            tableName = resultSet.getString("assetTableName");
        } else {
            throw new ConfigTableException("No table" + errorMsg);
        }

        // Each asset should have exactly one table name associated with it
        if (resultSet.next()) {
            throw new ConfigTableException("More then one table" + errorMsg);
        }

        return tableName;
    }
}

