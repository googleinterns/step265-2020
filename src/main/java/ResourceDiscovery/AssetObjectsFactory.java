package ResourceDiscovery;

import ResourceDiscovery.AssetObjects.*;

import java.util.Map;

/**
 * The AssetObjectsFactory is in charge of creating a specific AssetObject based on its assetType.
 */
public class AssetObjectsFactory {
    /**
     * This function is in charge of creating a specific AssetObject based on the provided assetTypeEnum
     * from the provided assetProperties.
     * @param assetTypeEnum - an enum which indicates which type of AssetObject should be created.
     * @param assetProperties - A map which contains the attributes of the AssetObject to be created.
     * @return an object of one of the classes the extends the AssetObject class.
     */
    public AssetObject createAssetObject(AssetType assetTypeEnum, Map<String, Object> assetProperties) {
        switch (assetTypeEnum) {
            case INSTANCE_COMPUTE_ASSET:
                return new InstanceComputeObject.Builder(assetProperties).build();
            case DISK_COMPUTE_ASSET:
                return new DiskComputeObject.Builder(assetProperties).build();
            case TOPIC_PUB_SUB_ASSET:
                return new TopicPubSubObject.Builder(assetProperties).build();
            case SUBSCRIPTION_PUB_SUB_ASSET:
                return new SubscriptionPubSubObject.Builder(assetProperties).build();
            case BUCKET_STORAGE_ASSET:
                return new BucketStorageObject.Builder(assetProperties).build();
            case INSTANCE_CLOUD_SQL_ASSET:
                return new InstanceCloudSqlObject.Builder(assetProperties).build();
        }
        return null;
    }
}
