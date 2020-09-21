package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetTypes;

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
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this TopicPubSubObject.
         */
        public Builder(Map<String,Object> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the TopicPubSubObject.
         * Fields that should be initialized for this object are: kind (manually generated) and name.
         * @return the newly initialized TopicPubSubObject
         */
        public TopicPubSubObject build() {
            // set AssetObject fields
            // set kind manually as this asset does not return it
            setKind(TOPIC_TYPE);
            setName(assetObjectsMap.get("name"));
            setAssetTypeEnum(AssetTypes.TOPIC_PUB_SUB_ASSET);

            // set specific asset type fields
            HashMap<String, Object> messageStoragePolicyMap = (HashMap<String, Object>) assetObjectsMap.get("messageStoragePolicy");
            specificObjectClass.allowedPersistenceRegions = (List<String>) messageStoragePolicyMap.get("allowedPersistenceRegions");
            return super.build();
        }
    }

    /**
     * Get the allowedPersistenceRegions field of this object.
     * @return A list of strings representing the allowedPersistenceRegions for this Topic Asset Object.
     */
    public List<String> getAllowedPersistenceRegions() {
        return this.allowedPersistenceRegions;
    }
}
