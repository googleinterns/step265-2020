package ResourceDiscovery;

import java.util.Map;

public class BucketStorageObject extends AssetObject {
    public BucketStorageObject(Map<String,String> assetObjectsMap) {
        setFieldValues(assetObjectsMap);
    }

    public void setFieldValues(Map<String,String> itemsMap) {
        this.kind = itemsMap.get("kind");
        this.name = itemsMap.get("name");
        this.id = itemsMap.get("id");
        this.zone = itemsMap.get("location");
        this.creationTime = convertStringToDate(itemsMap.get("timeCreated"));
    }
}
