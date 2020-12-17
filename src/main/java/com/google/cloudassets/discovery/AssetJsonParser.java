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
    private static final String NEXT_PAGE_KEY = "nextPageToken";
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private List<Map<String,Object>> assetsList;
    private List<String> zonesList;

    private Boolean hasNextPage;
    private String nextPageToken;

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
        findNextPageToken(jsonNode);
        Map<String, Object> propertiesMap = jsonMapper.convertValue(jsonNode, Map.class);

        // Some of the asset API return their properties in a slightly different json structure
        String assetKey = assetKind.getJsonParserKey();
        if (assetKind == AssetKind.APP_APP_ENGINE_ASSET) {
            this.assetsList.add(propertiesMap);
        } else if (assetKey != null) {
            this.assetsList = (List<Map<String,Object>>) propertiesMap.get(assetKey);
        } else {
            this.assetsList = (List<Map<String,Object>>) propertiesMap.get("items");
        }



        // In case the provided jsonNode returned no actual assets data, convert the assetList into
        // an emptyList in order not to fail the for loops which are using this list
        if (this.assetsList == null) {
            this.assetsList = Collections.emptyList();
        }
    }

    /**
     * This constructor function creates a list of strings in which each string represents a
     * different zone name.
     * @param jsonNode - a JsonNode object which returns from a HTTP GET request.
     * @param zoneJsonKey - a String representing the key name in which the list of zones can be
     *                    found in the jsonNode object.
     */
    public AssetJsonParser(JsonNode jsonNode, String zoneJsonKey) {
        this.zonesList = new ArrayList<>();
        findNextPageToken(jsonNode);

        for (JsonNode zoneNode : jsonNode.get(zoneJsonKey)) {
            this.zonesList.add(getStringFromNode(zoneNode.get("name")));
        }
    }

    /*
    This function checks if the given jsonNode have a nextPageToken field and sets the nextPageToken
    & hasNextPage accordingly
     */
    private void findNextPageToken(JsonNode jsonNode) {
        JsonNode nextPage = jsonNode.get(NEXT_PAGE_KEY);
        if (nextPage != null) {
            this.nextPageToken = getStringFromNode(nextPage);
        }
        this.hasNextPage = (this.nextPageToken != null);
    }

    /**
     * @return a list of maps in which each map represents the properties of a different AssetObject
     * that should be constructed
     */
    public List<Map<String,Object>> getAssetsList() {
        return this.assetsList;
    }

    /**
     * @return a list of strings in which each string represents a different zone name.
     */
    public List<String> getZonesList() {
        return this.zonesList;
    }

    /**
     * @return a Boolean representing weather or not this object has a nextPageToken.
     */
    public Boolean getHasNextPage() {
        return this.hasNextPage;
    }

    /**
     * @return a String representing the nextPageToken value for HTTP requests to APIs.
     */
    public String getNextPageToken() {
        return this.nextPageToken;
    }

    /*
    This function converts a given JsonNode into a String.
     */
    private String getStringFromNode(JsonNode jsonNode) {
        return jsonNode.toString().replaceAll("\"", "");
    }
}
