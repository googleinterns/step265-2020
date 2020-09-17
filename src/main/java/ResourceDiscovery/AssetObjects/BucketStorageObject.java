package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetTypes;
import com.google.cloud.Timestamp;

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
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this BucketStorageObject.
         */
        public Builder(Map<String,Object> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the BucketStorageObject.
         * Fields that should be initialized for this object are: kind, name, id, zone and
         * creationTime.
         * @return the newly initialized BucketStorageObject
         */
        public BucketStorageObject build() {
            // set AssetObject fields
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setId(assetObjectsMap.get("id"));
            setZone(getLastSeg(assetObjectsMap.get("location")));
            setCreationTime(convertStringToDate(assetObjectsMap.get("timeCreated")));
            setAssetTypeEnum(AssetTypes.BUCKET_STORAGE_ASSET);

            // set specific asset type fields
            specificObjectClass.storageClass = (String) assetObjectsMap.get("storageClass");
            specificObjectClass.updatedTime = convertStringToDate(assetObjectsMap.get("updated"));

            return super.build();
        }
    }

    /**
     * Get the storageClass field of this object.
     * @return A string representing the storageClass of this Bucket Asset Object.
     */
    public String getStorageClass() {
        return this.storageClass;
    }

    /**
     * Get the updatedTime field of this object.
     * @return A Timestamp representing the last time this Bucket Asset Object was updated.
     */
    public Timestamp getUpdatedTime() {
        return this.updatedTime;
    }
}
