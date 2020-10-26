package com.google.cloudassets.discovery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The AssetObjectsList class maps a json file into a list of Maps in which each map represents
 * a different asset object data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetObjectsList {
    private List<Map<String,Object>> assetObjectsList = Collections.emptyList();

    /**
     * This function creates a list of maps in which each map represents a AssetObject that should
     * be constructed.
     */
    @JsonAlias({"items", "topics", "subscriptions", "instances"})
    public void setAssetObjectsList(List items) {
        this.assetObjectsList = new ArrayList<>();
        for (Object item : items) {
            this.assetObjectsList.add((Map<String,Object>) item);
        }
    }

    /**
     * @return a list of maps in which each map represents a AssetObject that should be constructed
     */
    public List<Map<String,Object>> getAssetObjectsList() {
        return assetObjectsList;
    }
}
