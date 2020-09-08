package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class InstanceCloudSqlObject extends AssetObject {
    public static class Builder extends BaseBuilder<InstanceCloudSqlObject, InstanceCloudSqlObject.Builder> {
        /*
        This function returns a new InstanceCloudSqlObject.
         */
        protected InstanceCloudSqlObject getSpecificClass() {
            return new InstanceCloudSqlObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the InstanceCloudSqlObject class.
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this InstanceCloudSqlObject.
         */
        public Builder(Map<String,String> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the InstanceCloudSqlObject.
         * Fields that should be initialized for this object are: kind, name, type, zone and status.
         * @return the newly initialized InstanceCloudSqlObject
         */
        public InstanceCloudSqlObject build() {
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setType(getLastSeg(assetObjectsMap.get("databaseVersion")));
            setZone(getLastSeg(assetObjectsMap.get("region")));
            setStatus(assetObjectsMap.get("state"));
            return super.build();
        }
    }
}
