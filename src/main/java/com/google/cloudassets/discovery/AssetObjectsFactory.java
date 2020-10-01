package com.google.cloudassets.discovery;

import com.google.cloudassets.discovery.assetobjects.*;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.Map;

/**
 * The AssetObjectsFactory is in charge of creating a specific AssetObject based on its AssetKind.
 */
public class AssetObjectsFactory {
    /**
     * This function is in charge of creating a specific AssetObject based on the provided assetKind
     * from the provided assetProperties.
     * @param assetKind - an enum which indicates which type of AssetObject should be created.
     * @param assetProperties - A map which contains the attributes of the AssetObject to be created.
     * @param projectConfig - the relevant project configurations for these assets.
     * @return an object of one of the classes the extends the AssetObject class.
     */
    public AssetObject createAssetObject(AssetKind assetKind, Map<String, Object> assetProperties,
                                         ProjectConfig projectConfig) {
        switch (assetKind) {
            case INSTANCE_COMPUTE_ASSET:
                return new InstanceComputeObject.Builder(assetProperties, projectConfig).build();
            case DISK_COMPUTE_ASSET:
                return new DiskComputeObject.Builder(assetProperties, projectConfig).build();
            case TOPIC_PUB_SUB_ASSET:
                return new TopicPubSubObject.Builder(assetProperties, projectConfig).build();
            case SUBSCRIPTION_PUB_SUB_ASSET:
                return new SubscriptionPubSubObject.Builder(assetProperties, projectConfig).build();
            case BUCKET_STORAGE_ASSET:
                return new BucketStorageObject.Builder(assetProperties, projectConfig).build();
            case INSTANCE_CLOUD_SQL_ASSET:
                return new InstanceCloudSqlObject.Builder(assetProperties, projectConfig).build();
        }
        return null;
    }
}
