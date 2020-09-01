package ResourceDiscovery;

import java.util.Map;

public class InstanceCloudSqlObject extends AssetObject {
    public InstanceCloudSqlObject(Map<String,String> assetObjectsMap) {
        setFieldValues(assetObjectsMap);
    }

    public void setFieldValues(Map<String,String> itemsMap) {
        this.kind = itemsMap.get("kind");
        this.name = itemsMap.get("name");
//        this.id = itemsMap.get("etag");
        this.type = itemsMap.get("databaseVersion");
        this.zone = itemsMap.get("region");
        this.status = itemsMap.get("state");
    }
}
