package ResourceDiscovery;

import ResourceDiscovery.AssetObjects.AssetObject;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeyRange;
import com.google.cloud.spanner.KeySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.stereotype.Component;

@Component
public class SpannerTemplateAssets {
    @Autowired
    SpannerTemplate spannerTemplate;

    /**
     * This function deletes all the rows in the Assets table for the provided project.
     * @param accountId - a string of the account id for which the project assets will be deleted.
     * @param projectId - a string of the project id for which to delete the assets.
     */
    public void deleteProjectDataFromTable(String accountId, String projectId) {
        Key projectKey = Key.of(accountId, projectId);
        KeyRange projectKeyRange = KeyRange.closedClosed(projectKey, projectKey);

        this.spannerTemplate.delete(AssetObject.class, KeySet.range(projectKeyRange));
    }

    /**
     * This function inserts the provided assetObject into the Assets table.
     * @param assetObject - the AssetObject to be inserted.
     */
    public void insertAssetToTable(AssetObject assetObject) {
        this.spannerTemplate.insert(assetObject);
    }

}