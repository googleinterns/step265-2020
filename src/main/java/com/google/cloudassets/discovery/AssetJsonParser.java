package com.google.cloudassets.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The AssetJsonParser class maps a json node into a list of Maps in which each map represents
 * a different asset object data.
 */
public class AssetJsonParser {
    private List<Map<String,Object>> assetsList;
    private Map<String, Object> propertiesMap;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * This constructor function creates a list of maps in which each map represents a AssetObject
     * that should be constructed.
     */
    public AssetJsonParser(JsonNode jsonNode, AssetKind assetKind) {
        this.assetsList = new ArrayList<>();
        this.propertiesMap = jsonMapper.convertValue(jsonNode, Map.class);
        switch (assetKind) {
            case APP_APP_ENGINE_ASSET:
                this.assetsList.add(propertiesMap);
                break;
            case TOPIC_PUB_SUB_ASSET:
                this.assetsList = getFromPropertiesMap("topics");
                break;
            case SUBSCRIPTION_PUB_SUB_ASSET:
                this.assetsList = getFromPropertiesMap("subscriptions");
                break;
            case INSTANCE_SPANNER_ASSET:
                this.assetsList = getFromPropertiesMap("instances");
                break;
            case CLUSTER_KUBERNETES_ASSET:
                this.assetsList = getFromPropertiesMap("clusters");
                break;
            default:
                this.assetsList = getFromPropertiesMap("items");
                break;
        }
        if (this.assetsList == null) {
            this.assetsList = Collections.emptyList();
        }
    }

    /*
    This function returns a list of maps from the propertiesMap based on the given key string.
     */
    private List<Map<String,Object>> getFromPropertiesMap(String key) {
        return (List<Map<String,Object>>) this.propertiesMap.get(key);
    }

    /**
     * @return a list of maps in which each map represents a AssetObject that should be constructed.
     */
    public List<Map<String,Object>> getAssetsList() {
        return assetsList;
    }
}
