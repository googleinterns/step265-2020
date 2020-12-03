package com.google.cloudassets.discovery.projectobjects;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloudassets.discovery.*;
import com.google.cloudassets.discovery.assetobjects.AssetObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.flogger.FluentLogger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The ProjectAssetsMapper class is in charge of getting all of the different assets for the given
 * workspace ID & project ID.
 */
public class ProjectAssetsMapper {
    private static final String PROJECT_ID_EXP = "{project_id}";
    private static final String ZONE_NAME_EXP = "{zone_name}";
    private static final String ASSET_TYPE_EXP = "{asset_type}";
    private static final String API_ENABLED_STR = "ENABLED";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final AssetObjectsFactory assetObjectFactory = new AssetObjectsFactory();
    private static final HttpTransport requestFactory = new NetHttpTransport();

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final ProjectConfig projectConfig;
    private AccessToken accessToken;
    private GoogleCredentials credentials;


    /**
     * The ProjectAssetsMapper constructor initialized the relevant project configurations.
     * @param config the relevant project configurations.
     */
    public ProjectAssetsMapper(ProjectConfig config) {
        this.projectConfig = config;
        generateAccessToken();
    }

    /*
    This function creates a map of the data needed for the POST request for generating a new access
    token (a list of the scopes we need permission for).
     */
    private Map<String, List<String>> getScopeMap() {
        Map<String, List<String>> scopeMap =  new HashMap<>();
        List<String> scopesList = new ArrayList<>();
        scopesList.add("https://www.googleapis.com/auth/cloud-platform");
        scopeMap.put("scope", scopesList);
        return scopeMap;
    }

    /*
    This function generates an access token for the specific project's service account.
     */
    private void generateAccessToken() {
        String accessTokenUrl = "https://iamcredentials.googleapis.com/v1/projects/-/serviceAccounts/"
                            + this.projectConfig.getServiceAccountEmail() + ":generateAccessToken";
        try {
            // Build POST request to get AccessToken
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
            HttpRequest request = this.requestFactory.createRequestFactory(requestInitializer)
                    .buildPostRequest(new GenericUrl(accessTokenUrl), new JsonHttpContent(new JacksonFactory(), getScopeMap()));

            // Update newly generated AccessToken
            JsonNode jsonNode = jsonMapper.readTree(request.execute().parseAsString());
            Map<String, Object> accessTokenMap = jsonMapper.convertValue(jsonNode, Map.class);
            Date expireTime = DATE_FORMAT.parse((String) accessTokenMap.get("expireTime"));
            this.accessToken = new AccessToken((String) accessTokenMap.get("accessToken"), expireTime);
        } catch (IOException exception) {
            String errorMsg = "Encountered an IOException. Provided url was: " + accessTokenUrl;
            logger.atInfo().withCause(exception).log(errorMsg);
        } catch (ParseException exception) {
            String errorMsg = "Encountered a date parsing error while parsing 'expireTime' value. "
                            + "Dates should be in yyyy-MM-ddTHH:mm:ss format.";
            logger.atInfo().withCause(exception).log(errorMsg);
        }

    }

    /*
    This function creates the GoogleCredentials needed to access the different APIs and refreshes
    it if the access token has expired.
     */
    private void updateCredentials() {
        if (this.credentials == null) {
            this.credentials = new GoogleCredentials(this.accessToken);
        }

        // Refreshes the AccessToken used for the credentials if needed
        try {
            credentials.refreshIfExpired();
        } catch (IOException exception) {
            String errorMsg = "Encountered an IOException while trying to refresh the AccessToken "
                        + "of workspace ID: " + this.projectConfig.getWorkspaceId() ;
            logger.atInfo().withCause(exception).log(errorMsg);
        }
    }

    /*
     * This function returns a string representing the HttpResponse of the given HttpRequest url (in
     * json format).
     * @param assetListUrl - a string representing the url of a certain Google Cloud Api asset list
     * If an exception is caught, it logs the details to the logger and returns null.
     */
    private String getHttpInfo(String assetListUrl) {
        try {
            updateCredentials();
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(this.credentials);
            HttpRequest request = this.requestFactory.createRequestFactory(requestInitializer)
                                                .buildGetRequest(new GenericUrl(assetListUrl));
            return request.execute().parseAsString();
        } catch (IOException exception) {
            String errorMsg = "Encountered an IOException. Provided url was: " + assetListUrl;
            logger.atInfo().withCause(exception).log(errorMsg);
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
            JsonNode jsonNode = jsonMapper.readTree(getHttpInfo(assetListUrl));
            AssetJsonParser assetJsonParser = new AssetJsonParser(jsonNode, assetKind);

            for (Map<String,Object> assetProperties : assetJsonParser.getAssetsList()) {
                AssetObject assetObject = assetObjectFactory.createAssetObject(assetKind,
                                                                                assetProperties,
                                                                                projectConfig);
                assetObjectList.add(assetObject);
            }
        } catch (IOException exception) {
            String errorMsg = "Encountered an IOException while calling jsonMapper.readValue(). " +
                                "Provided url was: " + assetListUrl;
            logger.atInfo().withCause(exception).log(errorMsg);
        }
    }

    /*
    This function returns a list of strings of all of the zones in a certain project based on the
    provided zonesUrl string.
    If an exception is caught, it logs the details to the logger and returns an empty list.
     */
    private List<String> getZonesList(String zonesUrl, String zoneJsonKey) {
        List<String> zonesList = new ArrayList<>();
        try {
            JsonNode zonesJsonNode = jsonMapper.readTree(getHttpInfo(zonesUrl));
            for (JsonNode zoneNode : zonesJsonNode.get(zoneJsonKey)) {
                zonesList.add(zoneNode.get("name").toString().replaceAll("\"", ""));
            }
        } catch (IOException exception) {
            String errorMsg = "Encountered an IOException while calling jsonMapper.readTree(). " +
                                "Provided url was: " + zonesUrl;
            logger.atInfo().withCause(exception).log(errorMsg);
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
        getAllSpannerAssets(assetObjectList);
        getAllAppEngineAssets(assetObjectList);
        getAllKubernetesAssets(assetObjectList);

        return assetObjectList;
    }

    /*
    This function gets a String representing a specific apiService and checks whether or not it is
    enabled in this project.
     */
    private Boolean isApiEnabled(String apiService) {
        String url = ("https://serviceusage.googleapis.com/v1/projects/" + PROJECT_ID_EXP + "/services/"
                    + apiService).replace(PROJECT_ID_EXP, projectConfig.getProjectId());
        try {
            ApiDetails apiDetails = jsonMapper.readValue(getHttpInfo(url), ApiDetails.class);
            if (apiDetails.getApiState().equals(API_ENABLED_STR)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (IOException exception) {
            String errorMsg = "Encountered an IOException while calling jsonMapper.readValue(). " +
                    "Provided url was: " + apiService;
            logger.atInfo().withCause(exception).log(errorMsg);
        }
        return null;
    }

    /*
    This function adds the different Compute Asset Objects that belong to a specific Google Cloud
    project to the assetObjectList (if the compute API is enabled for this project).
     */
    private void getAllComputeAssets(List<AssetObject> assetObjectList) {
        String apiService = "compute.googleapis.com";
        if (isApiEnabled(apiService)) {
            String zonesComputeUrl = ("https://" + apiService + "/compute/v1/projects/" +
                    PROJECT_ID_EXP + "/zones").replace(PROJECT_ID_EXP, projectConfig.getProjectId());
            List<String> zonesList = getZonesList(zonesComputeUrl, "items");

            for (String zone : zonesList) {
                String computeUrl = (zonesComputeUrl + "/" + ZONE_NAME_EXP + "/" + ASSET_TYPE_EXP)
                        .replace(ZONE_NAME_EXP, zone);

                String instanceComputeUrl = computeUrl.replace(ASSET_TYPE_EXP, "instances");
                getAssetObjectList(assetObjectList, instanceComputeUrl, AssetKind.INSTANCE_COMPUTE_ASSET);

                String diskComputeUrl = computeUrl.replace(ASSET_TYPE_EXP, "disks");
                getAssetObjectList(assetObjectList, diskComputeUrl, AssetKind.DISK_COMPUTE_ASSET);
            }
        }
    }

    /*
    This function adds the different Pub Sub Asset Objects that belong to a specific Google Cloud
    project to the assetObjectList (if the pubsub API is enabled for this project).
     */
    private void getAllPubSubAssets(List<AssetObject> assetObjectList) {
        String apiService = "pubsub.googleapis.com";
        if (isApiEnabled(apiService)) {
            String pubSubUrl = ("https://" + apiService + "/v1/projects/" + PROJECT_ID_EXP + "/" +
                    ASSET_TYPE_EXP).replace(PROJECT_ID_EXP, projectConfig.getProjectId());

            String topicPubSubUrl = pubSubUrl.replace(ASSET_TYPE_EXP, "topics");
            getAssetObjectList(assetObjectList, topicPubSubUrl, AssetKind.TOPIC_PUB_SUB_ASSET);

            String subscriptionPubSubUrl = pubSubUrl.replace(ASSET_TYPE_EXP, "subscriptions");
            getAssetObjectList(assetObjectList, subscriptionPubSubUrl, AssetKind.SUBSCRIPTION_PUB_SUB_ASSET);
        }
    }

    /*
    This function adds the different Storage Asset Objects that belong to a specific Google Cloud
    project to the assetObjectList (if the storage API is enabled for this project).
     */
    private void getAllStorageAssets(List<AssetObject> assetObjectList) {
        String apiService = "storage.googleapis.com";
        if (isApiEnabled(apiService)) {
            String storageUrl = ("https://" + apiService + "/storage/v1/" + ASSET_TYPE_EXP +
                    "?project=" + PROJECT_ID_EXP).replace(PROJECT_ID_EXP, projectConfig.getProjectId());

            String bucketStorageUrl = storageUrl.replace(ASSET_TYPE_EXP, "b");
            getAssetObjectList(assetObjectList, bucketStorageUrl, AssetKind.BUCKET_STORAGE_ASSET);
        }
    }

    /*
    This function adds the different Cloud Sql Asset Objects that belong to a specific Google Cloud
    project to the assetObjectList (if the sqladmin API is enabled for this project).
     */
    private void getAllCloudSqlAssets(List<AssetObject> assetObjectList) {
        String apiService = "sqladmin.googleapis.com";
        if (isApiEnabled(apiService)) {
            String cloudSqlUrl = ("https://" + apiService + "/sql/v1beta4/projects/" +
                    PROJECT_ID_EXP + "/" + ASSET_TYPE_EXP).replace(PROJECT_ID_EXP, projectConfig.getProjectId());

            String instanceCloudSqlUrl = cloudSqlUrl.replace(ASSET_TYPE_EXP, "instances");
            getAssetObjectList(assetObjectList, instanceCloudSqlUrl, AssetKind.INSTANCE_CLOUD_SQL_ASSET);
        }
    }

    /*
    This function adds the different Spanner Asset Objects that belong to a specific Google Cloud
    project to the assetObjectList (if the spanner API is enabled for this project).
     */
    private void getAllSpannerAssets(List<AssetObject> assetObjectList) {
        String apiService = "spanner.googleapis.com";
        if (isApiEnabled(apiService)) {
            String spannerUrl = ("https://" + apiService + "/v1/projects/" + PROJECT_ID_EXP +
                    "/" + ASSET_TYPE_EXP).replace(PROJECT_ID_EXP, projectConfig.getProjectId());

            String instanceSpannerUrl = spannerUrl.replace(ASSET_TYPE_EXP, "instances");
            getAssetObjectList(assetObjectList, instanceSpannerUrl, AssetKind.INSTANCE_SPANNER_ASSET);
        }
    }

    /*
    This function adds the different App Engine Asset Objects that belong to a specific Google Cloud
    project to the assetObjectList (if the appengine API is enabled for this project).
     */
    private void getAllAppEngineAssets(List<AssetObject> assetObjectList) {
        String apiService = "appengine.googleapis.com";
        if (isApiEnabled(apiService)) {
            String appEngineUrl = ("https://" + apiService + "/v1/apps/" + PROJECT_ID_EXP)
                    .replace(PROJECT_ID_EXP, projectConfig.getProjectId());

            getAssetObjectList(assetObjectList, appEngineUrl, AssetKind.APP_APP_ENGINE_ASSET);
        }
    }

    /*
    This function adds the different Kubernetes Engine Asset Objects that belong to a specific Google
    Cloud project to the assetObjectList (if the container API is enabled for this project).
     */
    private void getAllKubernetesAssets(List<AssetObject> assetObjectList) {
        String apiService = "container.googleapis.com";
        if (isApiEnabled(apiService)) {
            String zonesKubernetesUrl = ("https://" + apiService + "/v1beta1/projects/" +
                    PROJECT_ID_EXP + "/locations").replace(PROJECT_ID_EXP, projectConfig.getProjectId());
            List<String> zonesList = getZonesList(zonesKubernetesUrl, "locations");

            for (String zone : zonesList) {
                String kubernetesUrl = (zonesKubernetesUrl + "/" + ZONE_NAME_EXP + "/clusters")
                        .replace(ZONE_NAME_EXP, zone);
                getAssetObjectList(assetObjectList, kubernetesUrl, AssetKind.CLUSTER_KUBERNETES_ASSET);
            }
        }
    }
}
