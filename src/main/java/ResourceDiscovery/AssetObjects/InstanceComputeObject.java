package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class InstanceComputeObject extends AssetObject {
    public InstanceComputeObject(Map<String,String> assetObjectsMap) {
        setFieldValues(assetObjectsMap);
    }

    public void setFieldValues(Map<String,String> itemsMap) {
        this.kind = itemsMap.get("kind");
        this.name = itemsMap.get("name");
        this.id = itemsMap.get("id");
        this.type = getLastSeg(itemsMap.get("machineType"));
        this.zone = getLastSeg(itemsMap.get("zone"));
        this.creationTime = convertStringToDate(itemsMap.get("creationTimestamp"));
        this.status = itemsMap.get("status");
    }
}
