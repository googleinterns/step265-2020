package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;

import java.util.HashMap;
import java.util.Map;

/**
 * The SubscriptionPubSubObject class represents the subscription asset in Google Cloud Pub Sub.
 */
public class SubscriptionPubSubObject extends AssetObject {
    private static final String SUBSCRIPTION_TYPE = "pubsub#subscription";

    private String topic;
    private String ttl;

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
         * @param assetProperties - a Map<String,String> which contains all of the relevant data for
         *                          this SubscriptionPubSubObject.
         */
        public Builder(Map<String,Object> assetProperties) {
            super(assetProperties);
        }

        /**
         * This function sets the relevant fields of the SubscriptionPubSubObject.
         * Fields that should be initialized for this object are: kind (manually generated) and name.
         * @return the newly initialized SubscriptionPubSubObject
         */
        public SubscriptionPubSubObject build() {
            // Set AssetObject fields
            // Set kind field manually as this asset does not return it
            setKind(SUBSCRIPTION_TYPE);
            setName(assetProperties.get("name"));
            setAssetTypeEnum(AssetType.SUBSCRIPTION_PUB_SUB_ASSET);

            // Set specific asset type fields
            specificObjectClass.topic = (String) assetProperties.get("topic");
            HashMap<String, Object> expirationPolicyMap = (HashMap<String, Object>) assetProperties.get("expirationPolicy");
            specificObjectClass.ttl = (String) expirationPolicyMap.get("ttl");
            return super.build();
        }
    }

    public String getTopic() {
        return this.topic;
    }

    public String getTtl() {
        return this.ttl;
    }
}
