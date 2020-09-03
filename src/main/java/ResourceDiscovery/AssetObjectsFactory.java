package ResourceDiscovery;

import ResourceDiscovery.AssetObjects.*;

import java.util.Map;

public class AssetObjectsFactory {
    /**
     * This function is in charge of creating a specific AssetObject based on the provided assetType
     * from the provided assetObjectsMap.
     * @param assetType - an enum which indicates which type of AssetObject should be created.
     * @param assetObjectsMap - A map which contains the attributes of the AssetObject to be created.
     * @return an object of one of the classes the extends the AssetObject class.
     */
    public AssetObject createAssetObject(Main.AssetTypes assetType, Map<String, String> assetObjectsMap) {
        switch (assetType) {
            case INSTANCE_COMPUTE_ASSET:
                return new InstanceComputeObject(assetObjectsMap);
            case DISK_COMPUTE_ASSET:
                return new DiskComputeObject(assetObjectsMap);
            case TOPIC_PUB_SUB_ASSET:
                return new TopicPubSubObject(assetObjectsMap);
            case SUBSCRIPTION_PUB_SUB_ASSET:
                return new SubscriptionPubSubObject(assetObjectsMap);
            case BUCKET_STORAGE_ASSET:
                return new BucketStorageObject(assetObjectsMap);
            case INSTANCE_CLOUD_SQL_ASSET:
                return new InstanceCloudSqlObject(assetObjectsMap);
        }
        return null;
    }
}
