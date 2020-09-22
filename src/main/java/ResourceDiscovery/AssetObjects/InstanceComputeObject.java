package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;

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
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this InstanceComputeObject.
         */
        public Builder(Map<String,Object> assetProperties) {
            super(assetProperties);
        }

        /**
         * This function sets the relevant fields of the InstanceComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, type, location,
         * creationTime and status.
         * @return the newly initialized InstanceComputeObject
         */
        public InstanceComputeObject build() {
            // Set AssetObject fields
            setKind(assetProperties.get("kind"));
            setName(assetProperties.get("name"));
            setId(assetProperties.get("id"));
            setType(getLastSeg(assetProperties.get("machineType")));
            setLocation(getLastSeg(assetProperties.get("zone")));
            setCreationTime(convertStringToDate(assetProperties.get("creationTimestamp")));
            setStatus(assetProperties.get("status"));
            setAssetTypeEnum(AssetType.INSTANCE_COMPUTE_ASSET);

            // Set specific asset type fields
            specificObjectClass.description = (String) assetProperties.get("description");
            specificObjectClass.canIpForward = (Boolean) assetProperties.get("canIpForward");
            specificObjectClass.cpuPlatform = (String) assetProperties.get("cpuPlatform");

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
