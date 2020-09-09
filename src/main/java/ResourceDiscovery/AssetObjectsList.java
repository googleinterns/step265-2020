package ResourceDiscovery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetObjectsList {
    private List<Map<String,String>> assetObjectsList = Collections.emptyList();

    /**
     * This function creates a list of maps in which each map represents a AssetObject that should
     * be constructed.
     */
    @JsonAlias({"items", "topics", "subscriptions"})
    public void setAssetObjectsList(List items) {
        this.assetObjectsList = new ArrayList<>();
        for (Object item : items) {
            this.assetObjectsList.add((Map<String,String>) item);
        }
    }

    /**
     * @return a list of maps in which each map represents a AssetObject that should be constructed
     */
    public List<Map<String,String>> getAssetObjectsList() {
        return assetObjectsList;
    }
}
