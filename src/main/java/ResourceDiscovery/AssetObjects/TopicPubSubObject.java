package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The TopicPubSubObject class represents the topic asset in Google Cloud Pub Sub.
 */
public class TopicPubSubObject extends AssetObject {
    private static final String TOPIC_TYPE = "pubsub#topic";

    private List<String> allowedPersistenceRegions;

    public static class Builder extends BaseBuilder<TopicPubSubObject, TopicPubSubObject.Builder> {
        /*
        This function returns a new TopicPubSubObject.
         */
        protected TopicPubSubObject getSpecificClass() {
            return new TopicPubSubObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the TopicPubSubObject class.
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this TopicPubSubObject.
         */
        public Builder(Map<String,Object> assetProperties) {
            super(assetProperties);
        }

        /**
         * This function sets the relevant fields of the TopicPubSubObject.
         * Fields that should be initialized for this object are: kind (manually generated) and name.
         * @return the newly initialized TopicPubSubObject
         */
        public TopicPubSubObject build() {
            // Set AssetObject fields
            // Set kind field manually as this asset does not return it
            setKind(TOPIC_TYPE);
            setName(assetProperties.get("name"));
            setAssetTypeEnum(AssetType.TOPIC_PUB_SUB_ASSET);

            // Set specific asset type fields
            HashMap<String, Object> messageStoragePolicyMap = (HashMap<String, Object>) assetProperties.get("messageStoragePolicy");
            specificObjectClass.allowedPersistenceRegions = (List<String>) messageStoragePolicyMap.get("allowedPersistenceRegions");
            return super.build();
        }
    }

    public List<String> getAllowedPersistenceRegions() {
        return this.allowedPersistenceRegions;
    }
}
