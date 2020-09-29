package com.google.cloudassets.discovery;

public enum AssetKind {
    BUCKET_STORAGE_ASSET("storage#bucket"),
    DISK_COMPUTE_ASSET("compute#disk"),
    INSTANCE_CLOUD_SQL_ASSET("sql#instance"),
    INSTANCE_COMPUTE_ASSET("compute#instance"),
    SUBSCRIPTION_PUB_SUB_ASSET("pubsub#subscription"),
    TOPIC_PUB_SUB_ASSET("pubsub#topic");

    private final String kindString;

    /*
    This private constructor initialized the fields for the given enum.
     */
    private AssetKind(String kind) {
        this.kindString = kind;
    }

    /**
     * @return a string representing the kind of this asset.
     */
    @Override
    public String toString() {
        return this.kindString;
    }
}

