package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * The SubscriptionPubSubObject class represents the subscription asset in Google Cloud Pub Sub.
 */
public class SubscriptionPubSubObject extends AssetObject {
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
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the SubscriptionPubSubObject.
         * Fields that should be initialized for this object are: kind and name.
         * @return the newly initialized SubscriptionPubSubObject
         */
        public SubscriptionPubSubObject build() {
            // Set AssetObject fields
            setKind(AssetKind.SUBSCRIPTION_PUB_SUB_ASSET);
            setName(getProperty("name"));

            // Set specific asset type fields
            specificObjectClass.topic = castToString(getProperty("topic"));
            HashMap<String, Object> expirationPolicyMap = castToMap(getProperty("expirationPolicy"));
            specificObjectClass.ttl = castToString(getProperty(expirationPolicyMap, "ttl"));

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
