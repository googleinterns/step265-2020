package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class DiskComputeObject extends AssetObject {
    public static class Builder extends BaseBuilder<DiskComputeObject, DiskComputeObject.Builder> {
        /*
        This function returns a new DiskComputeObject.
         */
        protected DiskComputeObject getSpecificClass() {
            return new DiskComputeObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the DiskComputeObject class.
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this DiskComputeObject.
         */
        public Builder(Map<String,String> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the DiskComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, type, zone,
         * creationTime and status.
         * @return the newly initialized DiskComputeObject
         */
        public DiskComputeObject build() {
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setId(assetObjectsMap.get("id"));
            setType(getLastSeg(assetObjectsMap.get("type")));
            setZone(getLastSeg(assetObjectsMap.get("zone")));
            setCreationTime(convertStringToDate(assetObjectsMap.get("creationTimestamp")));
            setStatus(assetObjectsMap.get("status"));
            return super.build();
        }
    }
}