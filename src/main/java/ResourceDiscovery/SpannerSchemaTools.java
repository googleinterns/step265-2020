package ResourceDiscovery;

import ResourceDiscovery.AssetObjects.AssetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerSchemaUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SpannerSchemaTools {
    @Autowired
    SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

    @Autowired
    SpannerSchemaUtils spannerSchemaUtils;

    /**
     * Creates the Assets table if it does not exist in spanner db.
     */
    public void createTableIfNotExists() {
        if (!this.spannerDatabaseAdminTemplate.tableExists("Assets")) {
            this.spannerDatabaseAdminTemplate.executeDdlStrings(
                    Collections.singleton(this.spannerSchemaUtils
                            .getCreateTableDdlString(AssetObject.class)), true);
        }
    }
}