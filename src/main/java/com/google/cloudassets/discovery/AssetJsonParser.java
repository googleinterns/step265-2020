package com.google.cloudassets.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The AssetJsonParser class maps a json node which returns from a HTTP GET request of a specific
 * asset API into a list of Maps in which each map represent a different asset object's data.
 */
public class AssetJsonParser {
    private List<Map<String,Object>> assetsList;
    private Map<String, Object> propertiesMap;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * This constructor function creates a list of maps in which each map represents the properties
     * of a different AssetObject that should be constructed. For example, the following json:
     *  {
     *   "subscriptions": [
     *     {
     *       "name": "projects/projectid/subscriptions/subscriptionname1",
     *     },
     *     {
     *       "name": "projects/projectid/subscriptions/subscriptionname2",
     *     },
     *   ]
     * }
     *
     *  would be parsed into a List containing two maps, each with one key named "name" and its value.
     * @param jsonNode - a JsonNode object which returns from a HTTP GET request and which contains
     *                 the properties of a given asset object.
     * @param assetKind - an AssetKind enum which represents for which asset kind the provided
     *                  properties belong.
     */
    public AssetJsonParser(JsonNode jsonNode, AssetKind assetKind) {
        this.assetsList = new ArrayList<>();
        this.propertiesMap = jsonMapper.convertValue(jsonNode, Map.class);

        // Some of the asset API return their properties in a slightly different json structure
        String assetKey = assetKind.getJsonParserKey();
        if (assetKind == AssetKind.APP_APP_ENGINE_ASSET) {
            this.assetsList.add(this.propertiesMap);
        } else if (assetKey != null) {
            this.assetsList = (List<Map<String,Object>>) this.propertiesMap.get(assetKey);
        } else {
            this.assetsList = (List<Map<String,Object>>) this.propertiesMap.get("items");
        }

        // In case the provided jsonNode returned no actual assets data, convert the assetList into
        // an emptyList in order not to fail the for loops which are using this list
        if (this.assetsList == null) {
            this.assetsList = Collections.emptyList();
        }
    }

    /**
     * @return a list of maps in which each map represents the properties of a different AssetObject
     * that should be constructed
     */
    public List<Map<String,Object>> getAssetsList() {
        return assetsList;
    }
}
