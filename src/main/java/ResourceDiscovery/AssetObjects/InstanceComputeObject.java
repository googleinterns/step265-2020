package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetTypes;

import java.util.Map;
import java.lang.String;

/**
 * The InstanceComputeObject class represents a VM instance asset in Google Cloud Compute.
 */
public class InstanceComputeObject extends AssetObject {
    private String description;
    private Boolean canIpForward;
    private String cpuPlatform;

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
        public Builder(Map<String,Object> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the InstanceComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, type, location,
         * creationTime and status.
         * @return the newly initialized InstanceComputeObject
         */
        public InstanceComputeObject build() {
            // Set AssetObject fields
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setId(assetObjectsMap.get("id"));
            setType(getLastSeg(assetObjectsMap.get("machineType")));
            setLocation(getLastSeg(assetObjectsMap.get("zone")));
            setCreationTime(convertStringToDate(assetObjectsMap.get("creationTimestamp")));
            setStatus(assetObjectsMap.get("status"));
            setAssetTypeEnum(AssetTypes.INSTANCE_COMPUTE_ASSET);

            // Set specific asset type fields
            specificObjectClass.description = (String) assetObjectsMap.get("description");
            specificObjectClass.canIpForward = (Boolean) assetObjectsMap.get("canIpForward");
            specificObjectClass.cpuPlatform = (String) assetObjectsMap.get("cpuPlatform");

            return super.build();
        }
    }

    /**
     * Get the description field of this object.
     * @return A string representing the description of this VM Asset Object.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get the canIpForward field of this object.
     * @return A Boolean representing if this VM Asset Object can ip forward.
     */
    public Boolean getCanIpForward() {
        return this.canIpForward;
    }

    /**
     * Get the cpuPlatform field of this object.
     * @return A string representing the cpu platform of this VM Asset Object.
     */
    public String getCpuPlatform() {
        return this.cpuPlatform;
    }
}
