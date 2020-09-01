package ResourceDiscovery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetObjectsList {
    private List<Map<String,String>> assetObjectsList;
    private int numOfAssets;

    /**
     * This function creates a list of maps in which each map represents a AssetObject that should
     * be constructed.
     */
    @JsonAlias({"items", "topics", "subscriptions"})
    public void setAssetObjectsList(List items) {
        this.assetObjectsList = new ArrayList<>();
        for (Object item : items) {
            this.assetObjectsList.add((Map<String,String>) item);
            numOfAssets++;
        }
    }

    /**
     * @return a list of maps in which each map represents a AssetObject that should be constructed
     */
    public List<Map<String,String>> getAssetObjectsList() {
        return assetObjectsList;
    }

    /**
     * @return then number of assets added to this object's assetObjectsList
     */
    public int getNumOfAssets() {
        return numOfAssets;
    }
}
