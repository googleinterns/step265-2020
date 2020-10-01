package com.google.cloudassets.discovery.projectobjects;

import com.google.api.client.http.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloudassets.discovery.assetobjects.AssetObject;
import com.google.cloudassets.discovery.AssetObjectsFactory;
import com.google.cloudassets.discovery.AssetObjectsList;
import com.google.cloudassets.discovery.AssetKind;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.flogger.FluentLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The ProjectAssetsMapper class is in charge of getting all of the different assets for the given
 * workspace ID & project ID.
 */
public class ProjectAssetsMapper {
    private static final String PROJECT_ID_EXP = "{project_id}";
    private static final String ZONE_NAME_EXP = "{zone_name}";
    private static final String ASSET_TYPE_EXP = "{asset_type}";

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final AssetObjectsFactory assetObjectFactory = new AssetObjectsFactory();

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private String workspaceId;
    private String projectId;


    /**
     * The ProjectAssetsMapper constructor initialized the workspace ID & project ID of this project
     * object.
     */
    public ProjectAssetsMapper() {
        this.workspaceId = ProjectConfig.getInstance().getWorkspaceId();
        this.projectId = ProjectConfig.getInstance().getProjectId();
    }

    /*
     * This function returns a string representing the HttpResponse of the given HttpRequest url (in
     * json format).
     * @param assetListUrl - a string representing the url of a certain Google Cloud Api asset list
     * @return
     * If an exception is caught, it logs the details to the logger and returns null.
     */
    private String getHttpInfo(String assetListUrl) {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
            HttpTransport requestFactory = new NetHttpTransport();

            HttpRequest request = requestFactory.createRequestFactory(requestInitializer)
                                                .buildGetRequest(new GenericUrl(assetListUrl));
            return request.execute().parseAsString();
        } catch (IOException exception) {
            String error_msg = "Encountered an IOException. Provided url was: " + assetListUrl;
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return null;
    }

    /*
     * This function creates a list of all of the AssetObjects of a given assetKind.
     * @param assetListUrl - a string representing the url of a certain Google Cloud Api asset list
     * @param assetKind - an enum from the AssetKind representing the relevant asset type that
     *                    should be listed.
     * @return
     * If an exception is caught, it logs the details to the logger.
     */
    private void getAssetObjectList(List<AssetObject> assetObjectList, String assetListUrl,
                                           AssetKind assetKind) {
        try {
            AssetObjectsList tempAssetObjectsList = jsonMapper.readValue(getHttpInfo(assetListUrl),
                                                                            AssetObjectsList.class);
             for (Map<String,Object> assetProperties : tempAssetObjectsList.getAssetObjectsList()) {
                AssetObject assetObject = assetObjectFactory.createAssetObject(assetKind,
                                                                                assetProperties);
                assetObjectList.add(assetObject);
            }
        } catch (IOException exception) {
            String error_msg = "Encountered an IOException while calling jsonMapper.readValue(). " +
                                "Provided url was: " + assetListUrl;
            logger.atInfo().withCause(exception).log(error_msg);
        }
    }

    /*
    This function returns a list of strings of all of the zones in a certain project based on the
    provided zonesUrl string.
    If an exception is caught, it logs the details to the logger and returns an empty list.
     */
    private List<String> getZonesList(String zonesUrl) {
        List<String> zonesList = new ArrayList<>();
        try {
            JsonNode zonesJsonNode = jsonMapper.readTree(getHttpInfo(zonesUrl));
            for (JsonNode zoneNode : zonesJsonNode.get("items")) {
                zonesList.add(zoneNode.get("name").toString().replaceAll("\"", ""));
            }
        } catch (IOException exception) {
            String error_msg = "Encountered an IOException while calling jsonMapper.readTree(). " +
                                "Provided url was: " + zonesUrl;
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return zonesList;
    }

    /**
     * This function creates and returns a list of the different AssetObjects that belong to a
     * specific Google Cloud project.
     * @return a list of the AssetObjects in a project.
     */
    public List<AssetObject> getAllAssets() {
        List<AssetObject> assetObjectList = new ArrayList<>();

        getAllComputeAssets(assetObjectList);
        getAllPubSubAssets(assetObjectList);
        getAllStorageAssets(assetObjectList);
        getAllCloudSqlAssets(assetObjectList);

        return assetObjectList;
    }

    /*
    This function returns a list of the different Compute Asset Objects that belong to a
    specific Google Cloud project.
     */
    private void getAllComputeAssets(List<AssetObject> assetObjectList) {
        String zonesComputeUrl = ("https://compute.googleapis.com/compute/v1/projects/" +
                                    PROJECT_ID_EXP + "/zones").replace(PROJECT_ID_EXP, projectId);
        List<String> zonesList = getZonesList(zonesComputeUrl);

        for (String zone : zonesList) {
            String computeUrl = (zonesComputeUrl + "/" + ZONE_NAME_EXP + "/" + ASSET_TYPE_EXP)
                                        .replace(ZONE_NAME_EXP, zone);

            String instanceComputeUrl = computeUrl.replace(ASSET_TYPE_EXP, "instances");
            getAssetObjectList(assetObjectList, instanceComputeUrl, AssetKind.INSTANCE_COMPUTE_ASSET);

            String diskComputeUrl = computeUrl.replace(ASSET_TYPE_EXP, "disks");
            getAssetObjectList(assetObjectList, diskComputeUrl, AssetKind.DISK_COMPUTE_ASSET);
        }
    }

    /*
    This function returns a list of the different Pub Sub Asset Objects that belong to a
    specific Google Cloud project.
     */
    private void getAllPubSubAssets(List<AssetObject> assetObjectList) {
        String pubSubUrl = ("https://pubsub.googleapis.com/v1/projects/" + PROJECT_ID_EXP + "/" +
                            ASSET_TYPE_EXP).replace(PROJECT_ID_EXP, projectId);

        String topicPubSubUrl = pubSubUrl.replace(ASSET_TYPE_EXP, "topics");
        getAssetObjectList(assetObjectList, topicPubSubUrl, AssetKind.TOPIC_PUB_SUB_ASSET);

        String subscriptionPubSubUrl = pubSubUrl.replace(ASSET_TYPE_EXP, "subscriptions");
        getAssetObjectList(assetObjectList, subscriptionPubSubUrl, AssetKind.SUBSCRIPTION_PUB_SUB_ASSET);
    }

    /*
    This function returns a list of the different Storage Asset Objects that belong to a
    specific Google Cloud project.
     */
    private void getAllStorageAssets(List<AssetObject> assetObjectList) {
        String storageUrl = ("https://storage.googleapis.com/storage/v1/" + ASSET_TYPE_EXP +
                            "?project=" + PROJECT_ID_EXP).replace(PROJECT_ID_EXP, projectId);

        String bucketStorageUrl = storageUrl.replace(ASSET_TYPE_EXP, "b");
        getAssetObjectList(assetObjectList, bucketStorageUrl, AssetKind.BUCKET_STORAGE_ASSET);
    }

    /*
    This function returns a list of the different Cloud Sql Asset Objects that belong to a
    specific Google Cloud project.
     */
    private void getAllCloudSqlAssets(List<AssetObject> assetObjectList) {
        String cloudSqlUrl = ("https://sqladmin.googleapis.com/sql/v1beta4/projects/" +
                            PROJECT_ID_EXP + "/" + ASSET_TYPE_EXP).replace(PROJECT_ID_EXP, projectId);

        String instanceCloudSqlUrl = cloudSqlUrl.replace(ASSET_TYPE_EXP, "instances");
        getAssetObjectList(assetObjectList, instanceCloudSqlUrl, AssetKind.INSTANCE_CLOUD_SQL_ASSET);
    }
}
