package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.Map;

/**
 * The AppAppEngineObject class represents an AppEngine application in Google Cloud.
 */
public class AppAppEngineObject extends AssetObject {
    private String authDomain;
    private String defaultHostname;
    private String codeBucket;
    private String gcrDomain;
    private String defaultBucket;
    private String databaseType;

    public static class Builder extends AssetObject.BaseBuilder<AppAppEngineObject, AppAppEngineObject.Builder> {
        /*
        This function returns a new AppAppEngineObject.
         */
        protected AppAppEngineObject getSpecificClass() {
            return new AppAppEngineObject();
        }

        /*
        This function returns this Builder.
         */
        protected AppAppEngineObject.Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the AppAppEngineObject class.
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this AppAppEngineObject.
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the AppAppEngineObject.
         * Fields that should be initialized for this object are: kind, name, id, location and status.
         * @return the newly initialized AppAppEngineObject
         */
        public AppAppEngineObject build() {
            // Set AssetObject fields
            setKind(AssetKind.APP_APP_ENGINE_ASSET);
            setName(assetProperties.get("name"));
            setId(assetProperties.get("id"));
            setLocation(getLastSeg(assetProperties.get("locationId")));
            setStatus(assetProperties.get("servingStatus"));

            // Set specific asset type fields
            specificObjectClass.authDomain = castToString(assetProperties.get("authDomain"));
            specificObjectClass.defaultHostname = castToString(assetProperties.get("defaultHostname"));
            specificObjectClass.codeBucket = castToString(assetProperties.get("codeBucket"));
            specificObjectClass.gcrDomain = castToString(assetProperties.get("gcrDomain"));
            specificObjectClass.defaultBucket = castToString(assetProperties.get("defaultBucket"));
            specificObjectClass.databaseType = castToString(assetProperties.get("databaseType"));
            return super.build();
        }
    }

    public String getAuthDomain() {
        return this.authDomain;
    }

    public String getDefaultHostname() {
        return this.defaultHostname;
    }

    public String getCodeBucket() {
        return this.codeBucket;
    }

    public String getGcrDomain() {
        return this.gcrDomain;
    }

    public String getDefaultBucket() {
        return this.defaultBucket;
    }

    public String getDatabaseType() {
        return this.databaseType;
    }
}