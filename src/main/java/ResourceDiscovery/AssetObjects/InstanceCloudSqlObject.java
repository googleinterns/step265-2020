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
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this InstanceCloudSqlObject.
         */
        public Builder(Map<String,Object> assetProperties) {
            super(assetProperties);
        }

        /**
         * This function sets the relevant fields of the InstanceCloudSqlObject.
         * Fields that should be initialized for this object are: kind, name, type, location and status.
         * @return the newly initialized InstanceCloudSqlObject
         */
        public InstanceCloudSqlObject build() {
            // Set AssetObject fields
            setKind(assetProperties.get("kind"));
            setName(assetProperties.get("name"));
            setType(getLastSeg(assetProperties.get("databaseVersion")));
            setLocation(getLastSeg(assetProperties.get("region")));
            setStatus(assetProperties.get("state"));
            setAssetTypeEnum(AssetType.INSTANCE_CLOUD_SQL_ASSET);

            // Set specific asset type fields
            specificObjectClass.etag = (String) assetProperties.get("etag");
            updateFieldsFromSettings();

            return super.build();
        }

        /*
        This function updates the following fields from this object's settings list: diskSizeGb,
        backupEnabled, replicationType & activationPolicy.
         */
        private void updateFieldsFromSettings() {
            HashMap<String, Object> settingsMap = (HashMap<String, Object>) assetProperties.get("settings");
            specificObjectClass.diskSizeGb = convertStringToInt(settingsMap.get("dataDiskSizeGb"));
            HashMap<String, Object> backupConfig = (HashMap<String, Object>) settingsMap.get("backupConfiguration");
            specificObjectClass.backupEnabled = (Boolean) backupConfig.get("enabled");
            specificObjectClass.replicationType = (String) settingsMap.get("replicationType");
            specificObjectClass.activationPolicy = (String) settingsMap.get("activationPolicy");
        }
    }

    public String getEtag() {
        return this.etag;
    }

    public int getDiskSizeGb() {
        return this.diskSizeGb;
    }

    public Boolean getBackupEnabled() {
        return this.backupEnabled;
    }

    public String getReplicationType() {
        return this.replicationType;
    }

    public String getActivationPolicy() {
        return this.activationPolicy;
    }

}
