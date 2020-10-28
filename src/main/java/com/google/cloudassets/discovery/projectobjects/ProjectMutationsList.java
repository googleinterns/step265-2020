package com.google.cloudassets.discovery.projectobjects;

import com.google.cloud.Timestamp;
import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.assetobjects.*;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Value;
import com.google.cloudassets.discovery.exceptions.ConfigTableException;
import com.google.cloudassets.discovery.exceptions.TableInsertionException;

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
     * @throws TableInsertionException if data could not be inserted to a certain table.
     */
    public List<Mutation> getMutationList(List<AssetObject> assetObjectList) throws TableInsertionException {
        String tableName;
        try {
            tableName = AssetKind.getMainTableName();
        } catch (ConfigTableException exception) {
            String errorMsg = "Could not insert data into the main table as its name could not be "
                            + "properly retrieved.";
            throw new TableInsertionException(errorMsg, exception);
        }

        for (AssetObject asset : assetObjectList) {
            this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("assetId").to(asset.getId())
                                .set("creationTime").to(asset.getCreationTime())
                                .set("status").to(asset.getStatus())
                                .set("location").to(asset.getLocation())
                                .build());

            // It is important that the insertion of the specific asset types happens after the
            // insertion of the AssetObject as the specific tables are interleaved with MAIN_TABLE.
            addSpecificAssetMutation(asset);
        }
        return this.mutations;
    }

    /*
    This function adds a new Mutation to the mutations list based on the specific AssetKind of the
    provided AssetObject.
    Throws a TableInsertionException if data could not be inserted to the given asset kind table.
     */
    private void addSpecificAssetMutation(AssetObject asset) throws TableInsertionException {
        String tableName;
        try {
            tableName = asset.getKindEnum().getAssetTableName();
        } catch (ConfigTableException exception) {
            String errorMsg = "Could not insert data into the following asset kind table: "
                            + asset.getKindEnum().toString() + ", as its name could not be properly retrieved.";
            throw new TableInsertionException(errorMsg, exception);
        }
        switch (asset.getKindEnum()) {
            case INSTANCE_COMPUTE_ASSET:
                InstanceComputeObject instanceComputeObject = (InstanceComputeObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("description").to(instanceComputeObject.getDescription())
                                .set("canIpForward").to(instanceComputeObject.getCanIpForward())
                                .set("cpuPlatform").to(instanceComputeObject.getCpuPlatform())
                                .set("machineType").to(instanceComputeObject.getMachineType())
                                .build());
                break;
            case DISK_COMPUTE_ASSET:
                DiskComputeObject diskComputeObject = (DiskComputeObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("diskSizeGb").to(diskComputeObject.getDiskSizeGb())
                                .set("updatedTime").to(diskComputeObject.getUpdatedTime())
                                .set("licenses").to(Value.stringArray(diskComputeObject.getLicenses()))
                                .set("type").to(diskComputeObject.getType())
                                .build());
                break;
            case BUCKET_STORAGE_ASSET:
                BucketStorageObject bucketStorageObject = (BucketStorageObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("storageClass").to(bucketStorageObject.getStorageClass())
                                .set("updatedTime").to(bucketStorageObject.getUpdatedTime())
                                .build());
                break;
            case INSTANCE_CLOUD_SQL_ASSET:
                InstanceCloudSqlObject instanceCloudSqlObject = (InstanceCloudSqlObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                                .set("etag").to(instanceCloudSqlObject.getEtag())
                                .set("diskSizeGb").to(instanceCloudSqlObject.getDiskSizeGb())
                                .set("backupEnabled").to(instanceCloudSqlObject.getBackupEnabled())
                                .set("replicationType").to(instanceCloudSqlObject.getReplicationType())
                                .set("activationPolicy").to(instanceCloudSqlObject.getActivationPolicy())
                                .set("databaseVersion").to(instanceCloudSqlObject.getDatabaseVersion())
                                .build());
                break;
            case SUBSCRIPTION_PUB_SUB_ASSET:
                SubscriptionPubSubObject subscriptionPubSubObject = (SubscriptionPubSubObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                        .set("topic").to(subscriptionPubSubObject.getTopic())
                        .set("ttl").to(subscriptionPubSubObject.getTtl())
                        .build());
                break;
            case TOPIC_PUB_SUB_ASSET:
                TopicPubSubObject topicPubSubObject = (TopicPubSubObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                        .set("allowedPersistenceRegions").to(Value.stringArray(topicPubSubObject.getAllowedPersistenceRegions()))
                        .build());
                break;
            case INSTANCE_SPANNER_ASSET:
                InstanceSpannerObject instanceSpannerObject = (InstanceSpannerObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                        .set("displayName").to(instanceSpannerObject.getDisplayName())
                        .set("nodeCount").to(instanceSpannerObject.getNodeCount())
                        .build());
                break;
            case APP_APP_ENGINE_ASSET:
                AppAppEngineObject appAppEngineObject = (AppAppEngineObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                        .set("authDomain").to(appAppEngineObject.getAuthDomain())
                        .set("defaultHostname").to(appAppEngineObject.getDefaultHostname())
                        .set("codeBucket").to(appAppEngineObject.getCodeBucket())
                        .set("gcrDomain").to(appAppEngineObject.getGcrDomain())
                        .set("defaultBucket").to(appAppEngineObject.getDefaultBucket())
                        .set("databaseType").to(appAppEngineObject.getDatabaseType())
                        .build());
                break;
            case CLUSTER_KUBERNETES_ASSET:
                ClusterKubernetesObject clusterKubernetesObject = (ClusterKubernetesObject) asset;
                this.mutations.add(setCommonColumnValues(tableName, asset)
                        .set("currentNodeCount").to(clusterKubernetesObject.getCurrentNodeCount())
                        .set("loggingService").to(clusterKubernetesObject.getLoggingService())
                        .set("monitoringService").to(clusterKubernetesObject.getMonitoringService())
                        .set("statusMessage").to(clusterKubernetesObject.getStatusMessage())
                        .set("expireTime").to(clusterKubernetesObject.getExpireTime())
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
                .set("workspaceId").to(asset.getWorkspaceId())
                .set("projectId").to(asset.getProjectId())
                .set("kind").to(asset.getKind())
                .set("assetName").to(asset.getName())
                .set("rowLastUpdateTime").to(Value.COMMIT_TIMESTAMP);
    }
}