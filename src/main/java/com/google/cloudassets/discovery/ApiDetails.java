package com.google.cloudassets.discovery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The ApiDetails class maps a json file and extracts its "state" field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDetails {
    private String apiState;

    /**
     * This function updates the apiState field from the json file.
     */
    @JsonAlias({"state"})
    public void setAssetObjectsList(String item) {
        apiState = item;
    }

    /**
     * @return this API's state.
     */
    public String getApiState() {
        return apiState;
    }
}
