package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;
import com.google.cloud.Timestamp;

import java.util.List;
import java.util.Map;

/**
 * The DiskComputeObject class represents a disk asset in Google Cloud Compute.
 */
public class DiskComputeObject extends AssetObject {
    private static final String DISK_KIND = "compute#disk";

    private int diskSizeGb;
    private Timestamp updatedTime;
    private List<String> licenses;

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
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this DiskComputeObject.
         */
        public Builder(Map<String,Object> assetProperties) {
            super(assetProperties);
        }

        /**
         * This function sets the relevant fields of the DiskComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, type, location,
         * creationTime and status.
         * @return the newly initialized DiskComputeObject
         */
        public DiskComputeObject build() {
            // Set AssetObject fields
            setKind(DISK_KIND);
            setName(assetProperties.get("name"));
            setId(assetProperties.get("id"));
            setType(getLastSeg(assetProperties.get("type")));
            setLocation(getLastSeg(assetProperties.get("zone")));
            setCreationTime(convertStringToDate(assetProperties.get("creationTimestamp")));
            setStatus(assetProperties.get("status"));
            setAssetTypeEnum(AssetType.DISK_COMPUTE_ASSET);

            // Set specific asset type fields
            specificObjectClass.diskSizeGb = convertStringToInt(assetProperties.get("sizeGb"));
            specificObjectClass.updatedTime = convertStringToDate(assetProperties.get("lastAttachTimestamp"));
            specificObjectClass.licenses = convertListToLastSegList(assetProperties.get("licenses"));

            return super.build();
        }
    }

    public int getDiskSizeGb() {
        return this.diskSizeGb;
    }

    public Timestamp getUpdatedTime() {
        return this.updatedTime;
    }

    public List<String> getLicenses() {
        return this.licenses;
    }
}
