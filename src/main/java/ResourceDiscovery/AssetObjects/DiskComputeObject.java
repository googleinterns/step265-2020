package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetTypes;
import com.google.cloud.Timestamp;

import java.util.List;
import java.util.Map;

/**
 * The DiskComputeObject class represents a disk asset in Google Cloud Compute.
 */
public class DiskComputeObject extends AssetObject {
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
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this DiskComputeObject.
         */
        public Builder(Map<String,Object> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the DiskComputeObject.
         * Fields that should be initialized for this object are: kind, name, id, type, zone,
         * creationTime and status.
         * @return the newly initialized DiskComputeObject
         */
        public DiskComputeObject build() {
            // set AssetObject fields
            setKind(assetObjectsMap.get("kind"));
            setName(assetObjectsMap.get("name"));
            setId(assetObjectsMap.get("id"));
            setType(getLastSeg(assetObjectsMap.get("type")));
            setZone(getLastSeg(assetObjectsMap.get("zone")));
            setCreationTime(convertStringToDate(assetObjectsMap.get("creationTimestamp")));
            setStatus(assetObjectsMap.get("status"));
            setAssetTypeEnum(AssetTypes.DISK_COMPUTE_ASSET);

            // set specific asset type fields
            specificObjectClass.diskSizeGb = convertStringToInt(assetObjectsMap.get("sizeGb"));
            specificObjectClass.updatedTime = convertStringToDate(assetObjectsMap.get("lastAttachTimestamp"));
            specificObjectClass.licenses = convertListToLastSegList(assetObjectsMap.get("licenses"));

            return super.build();
        }
    }
    /**
     * Get the diskSizeGb field of this object.
     * @return An string representing the disk size of this Disk Asset Object.
     */
    public int getDiskSizeGb() {
        return this.diskSizeGb;
    }

    /**
     * Get the updatedTime field of this object.
     * @return A Timestamp representing the last time this Disk Asset Object was updated.
     */
    public Timestamp getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * Get the licenses field of this object.
     * @return A list of string representing the licenses for this Disk Asset Object.
     */
    public List<String> getLicenses() {
        return this.licenses;
    }
}
