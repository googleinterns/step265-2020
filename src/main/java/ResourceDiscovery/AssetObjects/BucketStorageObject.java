package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;
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
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this BucketStorageObject.
         */
        public Builder(Map<String,Object> assetProperties) {
            super(assetProperties);
        }

        /**
         * This function sets the relevant fields of the BucketStorageObject.
         * Fields that should be initialized for this object are: kind, name, id, location and
         * creationTime.
         * @return the newly initialized BucketStorageObject
         */
        public BucketStorageObject build() {
            // Set AssetObject fields
            setKind(assetProperties.get("kind"));
            setName(assetProperties.get("name"));
            setId(assetProperties.get("id"));
            setLocation(getLastSeg(assetProperties.get("location")));
            setCreationTime(convertStringToDate(assetProperties.get("timeCreated")));
            setAssetTypeEnum(AssetType.BUCKET_STORAGE_ASSET);

            // Set specific asset type fields
            specificObjectClass.storageClass = (String) assetProperties.get("storageClass");
            specificObjectClass.updatedTime = convertStringToDate(assetProperties.get("updated"));

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
