package ResourceDiscovery.ProjectObjects;

import ResourceDiscovery.AssetObjects.*;
import ResourceDiscovery.AssetTables;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * The ProjectMutationsList is in charge of converting an AssetObject list (which can be generated
 * from the ProjectAssetsMapper class) into a Mutation list which contains all the assets of a
 * specific project as they should be inserted into the spanner db tables.
 */
public class ProjectMutationsList {
    private List<Mutation> mutations;

    /**
     * The constructor of the ProjectMutationsList which initialized a new Mutation list.
     */
    public ProjectMutationsList() {
        this.mutations = new ArrayList<>();
    }

    /**
     * This is the main function of this class and it is in charge of converting an AssetObject list
     * into a Mutation list.
     * @param assetObjectList - a list of AssetObjects to be converted.
     * @return a list of Mutations of the AssetObjects as they should be inserted into the spanner
     * db tables.
     */
    public List<Mutation> getMutationList(List<AssetObject> assetObjectList) {
        for (AssetObject asset : assetObjectList) {
            String tableName = AssetTables.MAIN_TABLE.getTableName();
            this.mutations.add(
                    setCommonColumnValues(tableName, asset)
                    .set("assetId").to(asset.getId())
                    .set("assetType").to(asset.getType())
                    .set("creationTime").to(asset.getCreationTime())
                    .set("status").to(asset.getStatus())
                    .set("zone").to(asset.getZone())
                    .build());

            // It is important that the insertion of the specific asset types happens after the
            // insertion of the AssetObject as the specific tables are interleaved with MAIN_TABLE
            addSpecificAssetMutation(asset);
        }
        return this.mutations;
    }

    /*
    This function adds a new Mutation to the mutations list based on the specific assetType of the
    provided AssetObject.
     */
    private void addSpecificAssetMutation(AssetObject asset) {
        String tableName;
        switch (asset.getAssetTypeEnum()) {
            case INSTANCE_COMPUTE_ASSET:
                tableName = AssetTables.INSTANCE_COMPUTE_TABLE.getTableName();
                InstanceComputeObject instanceComputeObject = (InstanceComputeObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("description").to(instanceComputeObject.getDescription())
                                .set("canIpForward").to(instanceComputeObject.getCanIpForward())
                                .set("cpuPlatform").to(instanceComputeObject.getCpuPlatform())
                                .build());
                break;
            case DISK_COMPUTE_ASSET:
                tableName = AssetTables.DISK_COMPUTE_TABLE.getTableName();
                DiskComputeObject diskComputeObject = (DiskComputeObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("diskSizeGb").to(diskComputeObject.getDiskSizeGb())
                                .set("updatedTime").to(diskComputeObject.getUpdatedTime())
                                .set("licenses").to(Value.stringArray(diskComputeObject.getLicenses()))
                                .build());
                break;
            case BUCKET_STORAGE_ASSET:
                tableName = AssetTables.BUCKET_STORAGE_TABLE.getTableName();
                BucketStorageObject bucketStorageObject = (BucketStorageObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("storageClass").to(bucketStorageObject.getStorageClass())
                                .set("updatedTime").to(bucketStorageObject.getUpdatedTime())
                                .build());
                break;
            case INSTANCE_CLOUD_SQL_ASSET:
                tableName = AssetTables.INSTANCE_CLOUD_SQL_TABLE.getTableName();
                InstanceCloudSqlObject instanceCloudSqlObject = (InstanceCloudSqlObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("etag").to(instanceCloudSqlObject.getEtag())
                                .set("diskSizeGb").to(instanceCloudSqlObject.getDiskSizeGb())
                                .set("backupEnabled").to(instanceCloudSqlObject.getBackupEnabled())
                                .set("replicationType").to(instanceCloudSqlObject.getReplicationType())
                                .set("activationPolicy").to(instanceCloudSqlObject.getActivationPolicy())
                                .build());
                break;
            case SUBSCRIPTION_PUB_SUB_ASSET:
                tableName = AssetTables.SUBSCRIPTION_PUB_SUB_TABLE.getTableName();
                SubscriptionPubSubObject subscriptionPubSubObject = (SubscriptionPubSubObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                        .set("topic").to(subscriptionPubSubObject.getTopic())
                        .set("ttl").to(subscriptionPubSubObject.getTtl())
                        .build());
                break;
            case TOPIC_PUB_SUB_ASSET:
                tableName = AssetTables.TOPIC_PUB_SUB_TABLE.getTableName();
                TopicPubSubObject topicPubSubObject = (TopicPubSubObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                        .set("allowedPersistenceRegions").to(Value.stringArray(topicPubSubObject.getAllowedPersistenceRegions()))
                        .build());
                break;
        }
    }

    /*
    This function creates a new Mutation for the provided table and sets all of the fields that are
    common to all of the asset tables.
     */
    private Mutation.WriteBuilder setCommonColumnValues(String tableName, AssetObject asset) {
        return Mutation.newInsertBuilder(tableName)
                .set("accountId").to(asset.getAccountId())
                .set("projectId").to(asset.getProjectId())
                .set("kind").to(asset.getKind())
                .set("assetName").to(asset.getName())
                .set("rowLastUpdateTime").to(Value.COMMIT_TIMESTAMP);
    }
}
