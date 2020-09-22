package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;

import java.util.HashMap;
import java.util.Map;

/**
 * The InstanceCloudSqlObject class represents an instance asset in Google Cloud Sql.
 */
public class InstanceCloudSqlObject extends AssetObject {
    private String etag;
    private int diskSizeGb;
    private Boolean backupEnabled;
    private String replicationType;
    private String activationPolicy;

    public static class Builder extends BaseBuilder<InstanceCloudSqlObject, InstanceCloudSqlObject.Builder> {
        /*
        This function returns a new InstanceCloudSqlObject.
         */
        protected InstanceCloudSqlObject getSpecificClass() {
            return new InstanceCloudSqlObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the InstanceCloudSqlObject class.
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this InstanceCloudSqlObject.
         */
        public Builder(Map<String,Object> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the InstanceCloudSqlObject.
         * Fields that should be initialized for this object are: kind, name, type, location and status.
         * @return the newly initialized InstanceCloudSqlObject
         */
        public InstanceCloudSqlObject build() {
            // Set AssetObject fields
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setType(getLastSeg(assetObjectsMap.get("databaseVersion")));
            setLocation(getLastSeg(assetObjectsMap.get("region")));
            setStatus(assetObjectsMap.get("state"));
            setAssetTypeEnum(AssetType.INSTANCE_CLOUD_SQL_ASSET);

            // Set specific asset type fields
            specificObjectClass.etag = (String) assetObjectsMap.get("etag");
            updateFieldsFromSettings();

            return super.build();
        }

        /*
        This function updates the following fields from this object's settings list: diskSizeGb,
        backupEnabled, replicationType & activationPolicy.
         */
        private void updateFieldsFromSettings() {
            HashMap<String, Object> settingsMap = (HashMap<String, Object>) assetObjectsMap.get("settings");
            specificObjectClass.diskSizeGb = convertStringToInt(settingsMap.get("dataDiskSizeGb"));
            HashMap<String, Object> backupConfig = (HashMap<String, Object>) settingsMap.get("backupConfiguration");
            specificObjectClass.backupEnabled = (Boolean) backupConfig.get("enabled");
            specificObjectClass.replicationType = (String) settingsMap.get("replicationType");
            specificObjectClass.activationPolicy = (String) settingsMap.get("activationPolicy");
        }
    }

    /**
     * Get the etag field of this object.
     * @return An string representing the etag of this Cloud Sql Asset Object.
     */
    public String getEtag() {
        return this.etag;
    }

    /**
     * Get the diskSizeGb field of this object.
     * @return An int representing the disk size of this Cloud Sql Asset Object.
     */
    public int getDiskSizeGb() {
        return this.diskSizeGb;
    }

    /**
     * Get the backupEnabled field of this object.
     * @return A Boolean representing if backup is enabled for this Cloud Sql Asset Object.
     */
    public Boolean getBackupEnabled() {
        return this.backupEnabled;
    }

    /**
     * Get the replicationType field of this object.
     * @return A string representing the replication type of this Cloud Sql Asset Object.
     */
    public String getReplicationType() {
        return this.replicationType;
    }

    /**
     * Get the activationPolicy field of this object.
     * @return A string representing the activation policy of this Cloud Sql Asset Object.
     */
    public String getActivationPolicy() {
        return this.activationPolicy;
    }

}
