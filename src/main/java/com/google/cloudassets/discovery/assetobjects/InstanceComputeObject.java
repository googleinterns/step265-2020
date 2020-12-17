package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.Map;
import java.lang.String;

/**
 * The InstanceComputeObject class represents a VM instance asset in Google Cloud Compute.
 */
public class InstanceComputeObject extends AssetObject {
    private String description;
    private Boolean canIpForward;
    private String cpuPlatform;
    private String machineType;

    public static class Builder extends BaseBuilder<InstanceComputeObject, Builder> {
        /*
        This function returns a new InstanceComputeObject.
         */
        protected InstanceComputeObject getSpecificClass() {
            return new InstanceComputeObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the InstanceComputeObject class.
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this InstanceComputeObject.
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the InstanceComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, location,
         * creationTime and status.
         * @return the newly initialized InstanceComputeObject
         */
        public InstanceComputeObject build() {
            // Set AssetObject fields
            setKind(AssetKind.INSTANCE_COMPUTE_ASSET);
            setName(getProperty("name"));
            setId(getProperty("id"));
            setLocation(getLastSeg(getProperty("zone")));
            setCreationTime(convertStringToDate(getProperty("creationTimestamp")));
            setStatus(getProperty("status"));

            // Set specific asset type fields
            specificObjectClass.description = castToString(getProperty("description"));
            specificObjectClass.canIpForward = castToBoolean(getProperty("canIpForward"));
            specificObjectClass.cpuPlatform = castToString(getProperty("cpuPlatform"));
            specificObjectClass.machineType = getLastSeg(getProperty("machineType"));

            return super.build();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getCanIpForward() {
        return this.canIpForward;
    }

    public String getCpuPlatform() {
        return this.cpuPlatform;
    }

    public String getMachineType() {
        return this.machineType;
    }
}
