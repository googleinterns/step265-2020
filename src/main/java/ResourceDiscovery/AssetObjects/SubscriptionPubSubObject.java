package ResourceDiscovery.AssetObjects;

import java.util.Map;

public class SubscriptionPubSubObject extends AssetObject {
    private static final String SUBSCRIPTION_TYPE = "pubsub#subscription";

    public static class Builder extends BaseBuilder<SubscriptionPubSubObject, SubscriptionPubSubObject.Builder> {
        /*
        This function returns a new SubscriptionPubSubObject.
         */
        protected SubscriptionPubSubObject getSpecificClass() {
            return new SubscriptionPubSubObject();
        }

        /*
        This function returns this Builder.
         */
        protected Builder getSpecificClassBuilder() {
            return this;
        }

        /**
         * This function returns a Builder object for the SubscriptionPubSubObject class.
         * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
         *                          this SubscriptionPubSubObject.
         */
        public Builder(Map<String,String> assetObjectsMap) {
            super(assetObjectsMap);
        }

        /**
         * This function sets the relevant fields of the SubscriptionPubSubObject.
         * Fields that should be initialized for this object are: kind (manually generated) and name.
         * @return the newly initialized SubscriptionPubSubObject
         */
        public SubscriptionPubSubObject build() {
            // set kind manually as this asset does not return it
            setKind(SUBSCRIPTION_TYPE);
            setName(assetObjectsMap.get("name"));
            return super.build();
        }
    }
}
