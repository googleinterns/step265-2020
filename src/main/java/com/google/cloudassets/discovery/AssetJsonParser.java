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

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * This constructor function creates a list of maps in which each map represents a AssetObject
     * that should be constructed.
     */
    public AssetJsonParser(JsonNode jsonNode, AssetKind assetKind) {
        this.assetsList = new ArrayList<>();
        Map<String, Object> propertiesMap = jsonMapper.convertValue(jsonNode, Map.class);
        switch (assetKind) {
            case APP_APP_ENGINE_ASSET:
                this.assetsList.add(propertiesMap);
                break;
            case TOPIC_PUB_SUB_ASSET:
                this.assetsList = (List<Map<String,Object>>) propertiesMap.get("topics");
                break;
            case SUBSCRIPTION_PUB_SUB_ASSET:
                this.assetsList = (List<Map<String,Object>>) propertiesMap.get("subscriptions");
                break;
            case INSTANCE_SPANNER_ASSET:
                this.assetsList = (List<Map<String,Object>>) propertiesMap.get("instances");
                break;
            default:
                this.assetsList = (List<Map<String,Object>>) propertiesMap.get("items");
                break;
        }
        if (this.assetsList == null) {
            this.assetsList = Collections.emptyList();
        }
    }

    /**
     * @return a list of maps in which each map represents a AssetObject that should be constructed.
     */
    public List<Map<String,Object>> getAssetsList() {
        return assetsList;
    }
}
