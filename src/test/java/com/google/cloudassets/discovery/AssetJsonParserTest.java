package com.google.cloudassets.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AssetJsonParserTest {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /*
    This helper function returns a JsonNode from the provided string that should represent a json
    file. If there is an exception while parsing the string the test will fail.
     */
    private JsonNode createJsonNode(String jsonString) {
        JsonNode jsonNode = null;
        try {
            jsonNode = jsonMapper.readTree(jsonString);
        } catch(IOException exception) {
            fail("Could not parse provided Json: " + jsonString);
        }
        return jsonNode;
    }

    /**
     * This function tests that a valid AssetJsonParser object is created from an empty json.
     */
    @Test
    public void testEmptyJsonParser() {
        String appEngineProperties = "{}";
        AssetJsonParser jsonParser = new AssetJsonParser(createJsonNode(appEngineProperties),
                                                        AssetKind.DISK_COMPUTE_ASSET);
        assertEquals(0, jsonParser.getAssetsList().size());
    }

    /**
     * This function tests that a valid AssetJsonParser object is created from a json containing
     * an empty Bucket asset list.
     */
    @Test
    public void testEmptyBucketJsonParser() {
        String appEngineProperties = "{\"kind\":\"storage#buckets\"}";
        AssetJsonParser jsonParser = new AssetJsonParser(createJsonNode(appEngineProperties),
                                                        AssetKind.BUCKET_STORAGE_ASSET);
        assertEquals(0, jsonParser.getAssetsList().size());
    }

    /**
     * This function tests that a valid AssetJsonParser object is created from a json containing
     * one AppEngine asset.
     */
    @Test
    public void testAppEngineJsonParser() {
        String appEngineProperties = "{\"name\":\"test\"}";
        AssetJsonParser jsonParser = new AssetJsonParser(createJsonNode(appEngineProperties),
                                                        AssetKind.APP_APP_ENGINE_ASSET);
        assertEquals(1, jsonParser.getAssetsList().size());
        assertEquals("test", jsonParser.getAssetsList().get(0).get("name"));
    }

    /**
     * This function tests that a valid AssetJsonParser object is created from a json containing
     * one Spanner asset.
     */
    @Test
    public void testSpannerJsonParser() {
        String appEngineProperties = "{\"instances\": [{\"name\": \"test\"}]}";
        AssetJsonParser jsonParser = new AssetJsonParser(createJsonNode(appEngineProperties),
                                                        AssetKind.INSTANCE_SPANNER_ASSET);
        assertEquals(1, jsonParser.getAssetsList().size());
        assertEquals("test", jsonParser.getAssetsList().get(0).get("name"));
    }

    /**
     * This function tests that a valid AssetJsonParser object is created from a json containing
     * two Bucket assets.
     */
    @Test
    public void testBucketJsonParser() {
        String appEngineProperties = "{\"kind\":\"storage#buckets\", \"items\": [{" +
                                    "\"kind\": \"storage#bucket\"," +
                                    "\"name\": \"test1\"}," +
                                    "{\"kind\": \"storage#bucket\"," +
                                    "\"name\": \"test2\"}]}";
        AssetJsonParser jsonParser = new AssetJsonParser(createJsonNode(appEngineProperties),
                                                        AssetKind.BUCKET_STORAGE_ASSET);
        assertEquals(2, jsonParser.getAssetsList().size());
        assertEquals("storage#bucket", jsonParser.getAssetsList().get(0).get("kind"));
        assertEquals("test2", jsonParser.getAssetsList().get(1).get("name"));
    }
}