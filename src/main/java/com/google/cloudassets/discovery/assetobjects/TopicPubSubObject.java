package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The TopicPubSubObject class represents the topic asset in Google Cloud Pub Sub.
 */
public class TopicPubSubObject extends AssetObject {
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
         * @param projectConfig - the relevant project configurations for this asset.
         */
        public Builder(Map<String,Object> assetProperties, ProjectConfig projectConfig) {
            super(assetProperties, projectConfig);
        }

        /**
         * This function sets the relevant fields of the TopicPubSubObject.
         * Fields that should be initialized for this object are: kind and name.
         * @return the newly initialized TopicPubSubObject
         */
        public TopicPubSubObject build() {
            // Set AssetObject fields
            setKind(AssetKind.TOPIC_PUB_SUB_ASSET);
            try {
                setName(assetProperties.get("name"));

                // Set specific asset type fields
                HashMap<String, Object> messageStoragePolicyMap = castToMap(assetProperties.get("messageStoragePolicy"));
                specificObjectClass.allowedPersistenceRegions = convertListToLastSegList(messageStoragePolicyMap.get("allowedPersistenceRegions"));
            } catch (NullPointerException exception) {
                logger.atInfo().withCause(exception).log("Could not set all of the TopicPubSubObject " +
                        "fields as one or more were missing. The provided map was: %s", assetProperties);
            }
            return super.build();
        }
    }

    public List<String> getAllowedPersistenceRegions() {
        return this.allowedPersistenceRegions;
    }
}
