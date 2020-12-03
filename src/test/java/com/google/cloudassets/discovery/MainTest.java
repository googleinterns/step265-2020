package com.google.cloudassets.discovery;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.ResultSet;
import com.google.cloudassets.discovery.exceptions.TableCreationException;
import com.google.cloudassets.discovery.exceptions.TableInsertionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class MainTest {
    private static final int FIVE_MINUTES = 5 * 60 * 1000;
    private static final int NUM_OF_EXPECTED_ASSETS = 3;
    private static final int NUM_OF_EXPECTED_BUCKETS = 3;
    private static final String STAGING_WORKSPACE_ID = "staging";
    private static final String STAGING_PROJECT_ID = "google.com:yarden-noa-2020";

    /*
    This function validates that the workspace ID & project Id which are used for this tests are
    properly activated in the configuration table. If not, the test fails and all of the other
    tests do not run.
     */
    @BeforeAll
    private static void validateStagingIsActive() {
        Main.initializeDbVars();
        String activeStagingWorkspaceQuery = "SELECT isActive, serviceAccountActive " +
                                            "FROM Workspace_Project_Table " +
                                            "WHERE workspaceId = '" + STAGING_WORKSPACE_ID + "' " +
                                            "AND projectId = '" + STAGING_PROJECT_ID + "'";
        ResultSet resultSet = Main.executeStringQuery(activeStagingWorkspaceQuery);

        if (resultSet.next()) {
            if (!resultSet.getBoolean("isActive") | !resultSet.getBoolean("serviceAccountActive")) {
                fail("Configurations in Workspace_Project_Table are not active for '" + STAGING_WORKSPACE_ID +
                        "' workspace ID and '" + STAGING_PROJECT_ID + "' project ID.");
            }
        }
        Main.closeDbVars();
    }

    /*
    This function checks if the row is from the current run (from the last five minutes) and fails
    the test if not as old data should be deleted from the table before new data is inserted and
    therefore there should not be old data for our staging workspace ID.
     */
    private void validateLastUpdateTime(Timestamp initialRunTime, ResultSet resultSet) {
        Timestamp lastUpdateTime = resultSet.getTimestamp("rowLastUpdateTime");
        if (lastUpdateTime.getSeconds() - initialRunTime.getSeconds() > FIVE_MINUTES) {
            fail("Data was not updated recently. Problematic row: " + resultSet.getCurrentRowAsStruct().toString());
        }
    }

    /*
    This function validates the data inserted into the asset main table.
     */
    private void testMainTableResults(Timestamp initialRunTime) {
        String mainAssetsQuery = "SELECT kind, rowLastUpdateTime FROM Main_Assets " +
                "WHERE workspaceId = '" + STAGING_WORKSPACE_ID + "'" +
                "AND projectId = '" + STAGING_PROJECT_ID + "'";
        ResultSet resultSet = Main.executeStringQuery(mainAssetsQuery);

        int numOfAssets = 0;
        while (resultSet.next()) {
            validateLastUpdateTime(initialRunTime, resultSet);
            numOfAssets += 1;
        }

        if (numOfAssets != NUM_OF_EXPECTED_ASSETS) {
            fail("Not the right amount of assets. Expected: " + NUM_OF_EXPECTED_BUCKETS +
                    ", Actual: " + numOfAssets);
        }
    }

    /*
    This function validates the data inserted into the bucket asset table.
    */
    private void testBucketTableResults(Timestamp initialRunTime) {
        String bucketAssetsQuery = "SELECT kind, rowLastUpdateTime, storageClass FROM Bucket_Storage_Assets " +
                                "WHERE workspaceId = '" + STAGING_WORKSPACE_ID + "'" +
                                "AND projectId = '" + STAGING_PROJECT_ID + "'";
        ResultSet resultSet = Main.executeStringQuery(bucketAssetsQuery);

        int numOfBuckets = 0;
        while (resultSet.next()) {
            validateLastUpdateTime(initialRunTime, resultSet);

            if (!resultSet.getString("storageClass").equals("STANDARD")) {
                fail("StorageClass field in Bucket_Storage_Assets table is different then expected. " +
                        "Expected: 'STANDARD', Actual: '" + resultSet.getString("storageClass") + "'");
            }

            if (resultSet.getString("kind").equals(AssetKind.BUCKET_STORAGE_ASSET.toString())) {
                numOfBuckets += 1;
            }
        }

        if (numOfBuckets != NUM_OF_EXPECTED_BUCKETS) {
            fail("Not the right amount of bucket assets. Expected: " + NUM_OF_EXPECTED_BUCKETS +
                    ", Actual: " + numOfBuckets);
        }
    }

    /**
     * This function runs the main function of the back-end. It then checks the relevant asset
     * tables to validate that the data of our test project was properly inserted.
     */
    @Test
    public void testProperProgramFlow() {
        try {
            Main.main(null);
            Timestamp initialRunTime = Timestamp.now();
            Main.initializeDbVars();

            testMainTableResults(initialRunTime);
            testBucketTableResults(initialRunTime);
        } catch (TableCreationException exception) {
            fail("Caught unexpected TableCreationException.");
        } catch (TableInsertionException exception) {
            fail("Caught unexpected TableInsertionException.");
        } finally {
            Main.closeDbVars();
        }
    }
}
