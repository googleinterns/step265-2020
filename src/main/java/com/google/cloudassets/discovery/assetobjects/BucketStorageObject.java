package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloud.Timestamp;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.Map;

/**
 * The BucketStorageObject class represents the bucket asset in Google Cloud Storage.
 */
public class BucketStorageObject extends AssetObject {
    private String storageClass;
    private Timestamp updatedTime;

    public static class Builder extends BaseBuilder<BucketStorageObject, BucketStorageObject.Builder> {
        /*
        This function returns a new BucketStorageObject.
         */
        protected BucketStorageObject getSpecificClass() {
            return new BucketStorageObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the BucketStorageObject class.
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this BucketStorageObject.
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the BucketStorageObject.
         * Fields that should be initialized for this object are: kind, name, id, location and
         * creationTime.
         * @return the newly initialized BucketStorageObject
         */
        public BucketStorageObject build() {
            // Set AssetObject fields
            setKind(AssetKind.BUCKET_STORAGE_ASSET);
            setName(getProperty("name"));
            setId(getProperty("id"));
            setLocation(getLastSeg(getProperty("location")));
            setCreationTime(convertStringToDate(getProperty("timeCreated")));

            // Set specific asset type fields
            specificObjectClass.storageClass = castToString(getProperty("storageClass"));
            specificObjectClass.updatedTime = convertStringToDate(getProperty("updated"));

            return super.build();
        }
    }

    public String getStorageClass() {
        return this.storageClass;
    }

    public Timestamp getUpdatedTime() {
        return this.updatedTime;
    }
}
