package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloud.Timestamp;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.List;
import java.util.Map;

/**
 * The DiskComputeObject class represents a disk asset in Google Cloud Compute.
 */
public class DiskComputeObject extends AssetObject {
    private int diskSizeGb;
    private Timestamp updatedTime;
    private List<String> licenses;
    private String type;

    public static class Builder extends BaseBuilder<DiskComputeObject, DiskComputeObject.Builder> {
        /*
        This function returns a new DiskComputeObject.
         */
        protected DiskComputeObject getSpecificClass() {
            return new DiskComputeObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the DiskComputeObject class.
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this DiskComputeObject.
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the DiskComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, location,
         * creationTime and status.
         * @return the newly initialized DiskComputeObject
         */
        public DiskComputeObject build() {
            // Set AssetObject fields
            setKind(AssetKind.DISK_COMPUTE_ASSET);
            setName(getProperty("name"));
            setId(getProperty("id"));
            setLocation(getLastSeg(getProperty("zone")));
            setCreationTime(convertStringToDate(getProperty("creationTimestamp")));
            setStatus(getProperty("status"));

            // Set specific asset type fields
            specificObjectClass.diskSizeGb = convertStringToInt(getProperty("sizeGb"));
            specificObjectClass.updatedTime = convertStringToDate(getProperty("lastAttachTimestamp"));
            specificObjectClass.licenses = convertListToLastSegList(getProperty("licenses"));
            specificObjectClass.type = getLastSeg(getProperty("type"));

            return super.build();
        }
    }

    public int getDiskSizeGb() {
        return this.diskSizeGb;
    }

    public Timestamp getUpdatedTime() {
        return this.updatedTime;
    }

    public List<String> getLicenses() {
        return this.licenses;
    }

    public String getType() {
        return this.type;
    }
}
