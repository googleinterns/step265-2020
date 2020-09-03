package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class TopicPubSubObject extends AssetObject {
    public TopicPubSubObject(Map<String,String> assetObjectsMap) {
        setFieldValues(assetObjectsMap);
    }

    public void setFieldValues(Map<String,String> itemsMap) {
        this.name = itemsMap.get("name");
    }
}
