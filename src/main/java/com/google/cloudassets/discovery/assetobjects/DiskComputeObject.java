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
            try {
                setName(assetProperties.get("name"));
                setId(assetProperties.get("id"));
                setLocation(getLastSeg(assetProperties.get("zone")));
                setCreationTime(convertStringToDate(assetProperties.get("creationTimestamp")));
                setStatus(assetProperties.get("status"));

                // Set specific asset type fields
                specificObjectClass.diskSizeGb = convertStringToInt(assetProperties.get("sizeGb"));
                specificObjectClass.updatedTime = convertStringToDate(assetProperties.get("lastAttachTimestamp"));
                specificObjectClass.licenses = convertListToLastSegList(assetProperties.get("licenses"));
                specificObjectClass.type = getLastSeg(assetProperties.get("type"));
            } catch (NullPointerException exception) {
                logger.atInfo().withCause(exception).log("Could not set all of the DiskComputeObject " +
                        "fields as one or more were missing. The provided map was: %s", assetProperties);
            }
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
