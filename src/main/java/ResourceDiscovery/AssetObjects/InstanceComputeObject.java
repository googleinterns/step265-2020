package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;

import java.util.Map;
import java.lang.String;

/**
 * The InstanceComputeObject class represents a VM instance asset in Google Cloud Compute.
 */
public class InstanceComputeObject extends AssetObject {
    private static final String INSTANCE_KIND = "compute#instance";

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
            setKind(INSTANCE_KIND);
            setName(assetProperties.get("name"));
            setId(assetProperties.get("id"));
            setType(getLastSeg(assetProperties.get("machineType")));
            setLocation(getLastSeg(assetProperties.get("zone")));
            setCreationTime(convertStringToDate(assetProperties.get("creationTimestamp")));
            setStatus(assetProperties.get("status"));
            setAssetTypeEnum(AssetType.INSTANCE_COMPUTE_ASSET);

            // Set specific asset type fields
            specificObjectClass.description = convertObjectToString(assetProperties.get("description"));
            specificObjectClass.canIpForward = convertObjectToBoolean(assetProperties.get("canIpForward"));
            specificObjectClass.cpuPlatform = convertObjectToString(assetProperties.get("cpuPlatform"));

            return super.build();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getCanIpForward() {
        return this.canIpForward;
    }

    public String getCpuPlatform() {
        return this.cpuPlatform;
    }
}
