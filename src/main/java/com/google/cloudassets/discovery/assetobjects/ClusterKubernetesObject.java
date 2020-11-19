package com.google.cloudassets.discovery.assetobjects;

import com.google.cloud.Timestamp;
import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.Map;

/**
 * The ClusterKubernetesObject class represents the cluster asset in Google Cloud Kubernetes Engine.
 */
public class ClusterKubernetesObject extends AssetObject {
    private int currentNodeCount;
    private String loggingService;
    private String monitoringService;
    private String statusMessage;
    private Timestamp expireTime;

    public static class Builder extends BaseBuilder<ClusterKubernetesObject, ClusterKubernetesObject.Builder> {
        /*
        This function returns a new ClusterKubernetesObject.
         */
        protected ClusterKubernetesObject getSpecificClass() {
            return new ClusterKubernetesObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the ClusterKubernetesObject class.
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this ClusterKubernetesObject.
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the ClusterKubernetesObject.
         * Fields that should be initialized for this object are: kind, name, location, creation
         * time and status.
         * @return the newly initialized ClusterKubernetesObject
         */
        public ClusterKubernetesObject build() {
            // Set AssetObject fields
            setKind(AssetKind.CLUSTER_KUBERNETES_ASSET);
            setName(assetProperties.get("name"));
            setLocation(assetProperties.get("location"));
            setCreationTime(convertStringToDate(assetProperties.get("createTime")));
            setStatus(assetProperties.get("status"));

            // Set specific asset type fields
            specificObjectClass.currentNodeCount = castToInt(assetProperties.get("currentNodeCount"));
            specificObjectClass.loggingService = castToString(assetProperties.get("loggingService"));
            specificObjectClass.monitoringService = castToString(assetProperties.get("monitoringService"));
            specificObjectClass.statusMessage = castToString(assetProperties.get("statusMessage"));
            specificObjectClass.expireTime = convertStringToDate(assetProperties.get("expireTime"));
            return super.build();
        }
    }

    public int getCurrentNodeCount() {
        return this.currentNodeCount;
    }

    public String getLoggingService() {
        return this.loggingService;
    }

    public String getMonitoringService() {
        return this.monitoringService;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public Timestamp getExpireTime() {
        return this.expireTime;
    }
}