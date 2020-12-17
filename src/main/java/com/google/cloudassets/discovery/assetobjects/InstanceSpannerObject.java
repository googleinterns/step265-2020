package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.Map;

/**
 * The InstanceSpannerObject class represents a spanner instance in Google Cloud.
 */
public class InstanceSpannerObject extends AssetObject {
    private String displayName;
    private int nodeCount;

    public static class Builder extends AssetObject.BaseBuilder<InstanceSpannerObject, InstanceSpannerObject.Builder> {
        /*
        This function returns a new InstanceSpannerObject.
         */
        protected InstanceSpannerObject getSpecificClass() {
            return new InstanceSpannerObject();
        }

        /*
        This function returns this Builder.
         */
        protected InstanceSpannerObject.Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the InstanceSpannerObject class.
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this InstanceSpannerObject.
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the InstanceSpannerObject.
         * Fields that should be initialized for this object are: kind, name, location and status.
         * @return the newly initialized InstanceSpannerObject
         */
        public InstanceSpannerObject build() {
            // Set AssetObject fields
            setKind(AssetKind.INSTANCE_SPANNER_ASSET);
            setName(getProperty("name"));
            setLocation(getLastSeg(getProperty("config")));
            setStatus(getProperty("state"));

            // Set specific asset type fields
            specificObjectClass.displayName = castToString(getProperty("displayName"));
            specificObjectClass.nodeCount = castToInt(getProperty("nodeCount"));

            return super.build();
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getNodeCount() {
        return this.nodeCount;
    }
}
