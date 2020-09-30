package resourceDisplay;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.common.flogger.FluentLogger;

import java.util.ArrayList;
import java.util.List;

public class AssetsController {
    private static final String SPANNER_PROJECT_ID = "noa-yarden-2020";
    private static final String SPANNER_INSTANCE_ID = "spanner1";
    private static final String SPANNER_DATABASE_ID = "db1";

    public List<TestAsset> getAllAssets(DatabaseClient dbClient) {

        List<TestAsset> allAssets = new ArrayList<>();
        Statement statement =
                Statement.newBuilder(
                        "SELECT assetName, assetType, location "
                                + "FROM Main_Assets")
                        .build();

        try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
            while (resultSet.next()) {
                TestAsset asset = new TestAsset();
                if (!resultSet.isNull("assetName")){
                    asset.setName(resultSet.getString("assetName"));
                }
                if (!resultSet.isNull("assetType")){
                    asset.setType(resultSet.getString("assetType"));
                }
                if (!resultSet.isNull("location")){
                    asset.setZone(resultSet.getString("location"));
                }
                allAssets.add(asset);
            }
            return allAssets;
        }
    }


}

