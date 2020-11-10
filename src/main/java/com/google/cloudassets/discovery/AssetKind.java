package com.google.cloudassets.discovery;

import com.google.cloud.spanner.ResultSet;
import com.google.cloudassets.discovery.exceptions.ConfigTableException;
import com.google.cloudassets.discovery.exceptions.NoTableConfigException;
import com.google.cloudassets.discovery.exceptions.TooManyTablesConfigException;

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
    TOPIC_PUB_SUB_ASSET("pubsub#topic");

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
        String queryStr = "SELECT DISTINCT assetTableName FROM Asset_Tables_Config WHERE assetKind = '"
                + this.kindString + "'";
        try {
            return getTableName(queryStr);
        } catch (NoTableConfigException exception) {
            String errorMsg = "No table was configured as the asset table in Asset_Tables_Config for the '"
                    + this.kindString + "' asset kind. Please make sure that exactly one table is configured"
                    + " for each asset kind.";
            throw new ConfigTableException(errorMsg, exception);
        } catch (TooManyTablesConfigException exception) {
            String errorMsg = "More than one table was was configured as the asset table in Asset_Tables_Config for the '"
                    + this.kindString + "' asset kind. Please make sure that exactly one table is configured"
                    + " for each asset kind.";
            throw new ConfigTableException(errorMsg, exception);
        }
    }

    /**
     * @return a string representing the main asset table name.
     * @throws ConfigTableException if the main table is not properly configured in the configuration
     * table.
     */
    public static String getMainTableName() throws ConfigTableException {
        String queryStr = "SELECT DISTINCT assetTableName FROM Asset_Tables_Config WHERE isMainTable = True";
        try {
            return getTableName(queryStr);
        } catch (NoTableConfigException exception) {
            String errorMsg = "No table was configured as the main table in Asset_Tables_Config. "
                    + "Please make sure that exactly one table has the 'isMainTable' flag on.";
            throw new ConfigTableException(errorMsg, exception);
        } catch (TooManyTablesConfigException exception) {
            String errorMsg = "More than one table was configured as the main table in Asset_Tables_Config. "
                    + "Please make sure that exactly one table has the 'isMainTable' flag on.";
            throw new ConfigTableException(errorMsg, exception);
        }
    }

    /*
    This function returns a table name returned from the provided queryStr. If there is not exactly
    one table name returned from the provided query it means that the Asset_Tables_Config is not
    configured properly and therefore unexpected behaviors may arise. In this case a
    NoTableConfigException / TooManyTablesConfigException is thrown.
     */
    private static String getTableName(String queryStr) throws NoTableConfigException, TooManyTablesConfigException {
        ResultSet resultSet = executeStringQuery(queryStr);
        String tableName = null;
        if (resultSet.next()) {
            tableName = resultSet.getString("assetTableName");
        } else {
            throw new NoTableConfigException();
        }

        // Each asset should have exactly one table name associated with it
        if (resultSet.next()) {
            throw new TooManyTablesConfigException();
        }

        return tableName;
    }
}

