package com.google.cloudassets.discovery.assetobjects;

import com.google.cloudassets.discovery.AssetKind;
import com.google.cloudassets.discovery.projectobjects.ProjectConfig;
import com.google.cloud.Timestamp;
import com.google.common.flogger.FluentLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The AssetObject class is an abstract class which is the parent of all of the specific asset
 * object classes. This class provides setters and getters to all of the common fields of the
 * asset objects. In addition it provides some helper functions for specific type parsing and
 * conversions which are used in several asset object classes (such as a convertStringToDate function).
 */
abstract public class AssetObject {
    // Asset primary keys
    protected String workspaceId;
    protected String projectId;
    // This field stores an enum representing the specific asset kind, but in the DB it is stored
    // as a string.
    protected AssetKind kind;
    protected String name;

    // Asset additional data
    protected String id;
    protected String location;
    protected Timestamp creationTime;
    protected String status;

    protected static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static final Pattern LAST_SEGMENT_PATTERN = Pattern.compile("/?([^/]*$)");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected abstract static class BaseBuilder<T extends AssetObject, B extends BaseBuilder> {
        protected Map<String,Object> assetProperties;
        protected T specificObjectClass;
        protected B specificObjectClassBuilder;

        protected abstract T getSpecificClass();
        protected abstract B getSpecificClassBuilder();

        /*
         * The constructor of the BaseBuilder class which set the relevant objects for the specific
         * object.
         * @param assetMap - a Map<String,String> of the relevant asset properties.
         * @param projectConfig - the relevant project configurations for this asset.
         */
        protected BaseBuilder(Map<String,Object> assetMap, ProjectConfig projectConfig) {
            specificObjectClass = getSpecificClass();
            specificObjectClass.workspaceId = projectConfig.getWorkspaceId();
            specificObjectClass.projectId = projectConfig.getProjectId();

            specificObjectClassBuilder = getSpecificClassBuilder();
            assetProperties = assetMap;
        }

        /*
        Set the kind field of this object with the provided AssetKind and return its specific Builder.
        */
        public B setKind(Object kind) {
            specificObjectClass.kind = (AssetKind) kind;
            return specificObjectClassBuilder;
        }

        /*
        Set the name field of this object with the provided string and return its specific Builder.
        */
        public B setName(Object name) {
            specificObjectClass.name = castToString(name);
            return specificObjectClassBuilder;
        }

        /*
        Set the id field of this object with the provided string and return its specific Builder.
        */
        public B setId(Object id) {
            specificObjectClass.id = castToString(id);
            return specificObjectClassBuilder;
        }

        /*
        Set the location field of this object with the provided string and return its specific Builder.
        */
        public B setLocation(Object location) {
            specificObjectClass.location = castToString(location);
            return specificObjectClassBuilder;
        }

        /*
        Set the creationTime field of this object with the provided string and return its specific Builder.
        */
        public B setCreationTime(Timestamp creationTime) {
            specificObjectClass.creationTime = creationTime;
            return specificObjectClassBuilder;
        }

        /*
        Set the status field of this object with the provided string and return its specific Builder.
        */
        public B setStatus(Object status) {
            specificObjectClass.status = castToString(status);
            return specificObjectClassBuilder;
        }

        /*
        This function returns the specific asset object.
         */
        public T build() {
            return specificObjectClass;
        }
    }

    // AssetObject Class Getters
    public String getWorkspaceId() {
        return this.workspaceId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public String getKind() {
        return this.kind.toString();
    }

    public AssetKind getKindEnum() {
        return this.kind;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getLocation() {
        return this.location;
    }

    public Timestamp getCreationTime() {
        return this.creationTime;
    }

    public String getStatus() {
        return this.status;
    }

    /*
     * This function receives a string representing a url and extracts the string after the last '/'
     * char in the url.
     * If the provided string does not match that pattern the original string is returned.
     */
    protected static String getLastSeg(Object url) {
        try {
            String urlToParse = (String) url;
            Matcher matcher = LAST_SEGMENT_PATTERN.matcher(urlToParse);
            if (matcher.find()) {
                // Return the grouped part of the regex (not including the last "/" char)
                return matcher.group(1);
            }
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into a string. Received object: %s", url);
        }
        return null;
    }

    /*
     * This function receives a list of strings representing urls and returns a list of strings
     * with the last segment for each url.
     */
    protected static List<String> convertListToLastSegList(Object urlsObject) {
        try {
            List<String> urlsList = (List<String>) urlsObject;
            return urlsList.stream().map(AssetObject::getLastSeg).collect(Collectors.toList());
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into a list of strings. Received object: %s", urlsObject);
        }
        return null;
    }

    /*
    This function receives a string representing a date and time and returns it as a Date object.
    The provided dateString should be in the following format: yyyy-MM-ddTHH:mm:ss
    If the provided dateString does not match this format null is returned and details are logged.
     */
    protected static Timestamp convertStringToDate(Object dateString) {
        try {
            return Timestamp.of(DATE_FORMAT.parse((String) dateString));
        } catch (ParseException exception) {
            logger.atInfo().withCause(exception).log("Encountered a date parsing error. " +
                    "Dates should be in yyyy-MM-ddTHH:mm:ss format, provided date: %s", dateString);
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into a string. Received object: %s", dateString);
        }
        return null;
    }

    /*
    This function receives a string representing an int and returns it as an Integer.
    If the provided intString does not match this format null is returned and details are logged.
     */
    protected static Integer convertStringToInt(Object intString) {
        try {
            return Integer.valueOf((String) intString);
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into a string. Received object: %s", intString);
        } catch (NumberFormatException exception) {
            logger.atInfo().withCause(exception).log("Encountered a formatting error, expected to get an " +
                    "object that can be casted into an Integer. Received object: %s", intString);
        }
        return null;
    }

    /*
    This function receives an Object representing a string and returns it as a String.
    If the provided stringToCast can not be casted into a String, null is returned and details
    are logged.
     */
    protected static String castToString(Object stringToCast) {
        try {
            return (String) stringToCast;
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into a string. Received object: %s", stringToCast);
        }
        return null;
    }

    /*
    This function receives an Object representing a boolean and returns it as a Boolean.
    If the provided booleanToCast can not be casted into a Boolean, null is returned and details
    are logged.
     */
    protected static Boolean castToBoolean(Object booleanToCast) {
        try {
            return (Boolean) booleanToCast;
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into a boolean. Received object: %s", booleanToCast);
        }
        return null;
    }

    /*
    This function receives an Object and returns it as a HashMap<String, Object>.
    If the provided mapToCast can not be casted into a  HashMap<String, Object>, null is returned
    and details are logged.
     */
    protected static HashMap<String, Object> castToMap(Object mapToCast) {
        try {
            return (HashMap<String, Object>) mapToCast;
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into a HashMap<String, Object>. Received object: %s", mapToCast);
        }
        return null;
    }

    /*
    This function receives an Object representing an int and returns it as an Integer.
    If the provided intToCast can not be casted into an int, null is returned and details
    are logged.
     */
    protected static Integer castToInt(Object intToCast) {
        try {
            return (Integer) intToCast;
        } catch (ClassCastException exception) {
            logger.atInfo().withCause(exception).log("Encountered a casting error, expected to get an " +
                    "object that can be casted into an Integer. Received object: %s", intToCast);
        }
        return null;
    }

}