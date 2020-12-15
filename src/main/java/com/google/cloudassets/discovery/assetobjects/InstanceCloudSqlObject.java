package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

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
    private String databaseVersion;

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
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the InstanceCloudSqlObject.
         * Fields that should be initialized for this object are: kind, name, location and status.
         * @return the newly initialized InstanceCloudSqlObject
         */
        public InstanceCloudSqlObject build() {
            // Set AssetObject fields
            setKind(AssetKind.INSTANCE_CLOUD_SQL_ASSET);
            setName(getProperty("name"));
            setLocation(getLastSeg(getProperty("region")));
            setStatus(getProperty("state"));

            // Set specific asset type fields
            specificObjectClass.etag = (String) getProperty("etag");
            updateFieldsFromSettings();
            specificObjectClass.databaseVersion = getLastSeg(getProperty("databaseVersion"));

            return super.build();
        }

        /*
        This function updates the following fields from this object's settings list: diskSizeGb,
        backupEnabled, replicationType & activationPolicy.
         */
        private void updateFieldsFromSettings() {
            HashMap<String, Object> settingsMap = castToMap(getProperty("settings"));
            specificObjectClass.diskSizeGb = convertStringToInt(getProperty(settingsMap, "dataDiskSizeGb"));
            HashMap<String, Object> backupConfig = castToMap(getProperty(settingsMap, "backupConfiguration"));
            specificObjectClass.backupEnabled = castToBoolean(getProperty(backupConfig, "enabled"));
            specificObjectClass.replicationType = castToString(getProperty(settingsMap, "replicationType"));
            specificObjectClass.activationPolicy = castToString(getProperty(settingsMap, "activationPolicy"));
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

    public String getDatabaseVersion() {
        return this.databaseVersion;
    }

}
