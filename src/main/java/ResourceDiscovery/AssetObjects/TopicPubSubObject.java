package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class TopicPubSubObject extends AssetObject {
    private static final String TOPIC_TYPE = "pubsub#topic";

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
        public Builder(Map<String,String> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the TopicPubSubObject.
         * Fields that should be initialized for this object are: kind (manually generated) and name.
         * @return the newly initialized TopicPubSubObject
         */
        public TopicPubSubObject build() {
            // set kind manually as this asset does not return it
            setKind(TOPIC_TYPE);
            setName(assetObjectsMap.get("name"));
            return super.build();
        }
    }
}
