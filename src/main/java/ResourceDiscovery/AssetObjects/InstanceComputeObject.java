package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class InstanceComputeObject extends AssetObject {
    public static class Builder extends BaseBuilder<InstanceComputeObject, Builder> {
        /*
        This function returns a new InstanceComputeObject.
         */
        protected InstanceComputeObject getSpecificClass() {
            return new InstanceComputeObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the InstanceComputeObject class.
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this InstanceComputeObject.
         */
        public Builder(Map<String,String> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the InstanceComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, type, zone,
         * creationTime and status.
         * @return the newly initialized InstanceComputeObject
         */
        public InstanceComputeObject build() {
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setId(assetObjectsMap.get("id"));
            setType(getLastSeg(assetObjectsMap.get("machineType")));
            setZone(getLastSeg(assetObjectsMap.get("zone")));
            setCreationTime(convertStringToDate(assetObjectsMap.get("creationTimestamp")));
            setStatus(assetObjectsMap.get("status"));
            return super.build();
        }
    }
}
