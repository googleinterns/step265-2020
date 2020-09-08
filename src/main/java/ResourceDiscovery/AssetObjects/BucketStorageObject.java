package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class BucketStorageObject extends AssetObject {
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
        public Builder(Map<String,String> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the BucketStorageObject.
         * Fields that should be initialized for this object are: kind, name, id, zone and
         * creationTime.
         * @return the newly initialized BucketStorageObject
         */
        public BucketStorageObject build() {
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setId(assetObjectsMap.get("id"));
            setZone(getLastSeg(assetObjectsMap.get("location")));
            setCreationTime(convertStringToDate(assetObjectsMap.get("timeCreated")));
            return super.build();
        }
    }
}
