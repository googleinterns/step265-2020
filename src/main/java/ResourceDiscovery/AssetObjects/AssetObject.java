package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetType;
import ResourceDiscovery.ProjectObjects.ProjectConfig;
import com.google.cloud.Timestamp;
import com.google.common.flogger.FluentLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The AssetObject class is an abstract class which is the parent of all of the specific asset
 * object classes. This class provides setters and getters to all of the common fields of the
 * asset objects. In addition it provides some helper functions for specific type parsing and
 * conversions which are used in several asset object classes (such as a convertStringToDate function).
 */
abstract public class AssetObject {
    // Asset primary keys
    public String accountId = ProjectConfig.getInstance().getAccountId();
    public String projectId = ProjectConfig.getInstance().getProjectId();
    protected String kind;
    protected String name;

    // Asset additional data
    protected String id;
    protected String type;
    protected String location;
    protected Timestamp creationTime;
    protected String status;

    // This field corresponds with the "kind" field as they both indicate the asset specific type,
    // the "kind" field is usually provided by the asset api and therefore we needed the
    // "assetTypeEnum" field as well which is fully in our control and not prone to unexpected changes.
    protected AssetType assetTypeEnum;

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
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
         */
        protected BaseBuilder(Map<String,Object> assetMap) {
            specificObjectClass = getSpecificClass();
            specificObjectClassBuilder = getSpecificClassBuilder();
            assetProperties = assetMap;
        }

        /*
        Set the kind field of this object with the provided string and return its specific Builder.
        */
        public B setKind(Object kind) {
            specificObjectClass.kind = (String) kind;
            return specificObjectClassBuilder;
        }

        /*
        Set the name field of this object with the provided string and return its specific Builder.
        */
        public B setName(Object name) {
            specificObjectClass.name = (String) name;
            return specificObjectClassBuilder;
        }

        /*
        Set the id field of this object with the provided string and return its specific Builder.
        */
        public B setId(Object id) {
            specificObjectClass.id = (String) id;
            return specificObjectClassBuilder;
        }

        /*
        Set the type field of this object with the provided string and return its specific Builder.
        */
        public B setType(Object type) {
            specificObjectClass.type = (String) type;
            return specificObjectClassBuilder;
        }

        /*
        Set the location field of this object with the provided string and return its specific Builder.
        */
        public B setLocation(Object location) {
            specificObjectClass.location = (String) location;
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
            specificObjectClass.status = (String) status;
            return specificObjectClassBuilder;
        }

        /*
        Set the status assetTypeEnum of this object with the provided enum and return its specific Builder.
        */
        public B setAssetTypeEnum(AssetType assetType) {
            specificObjectClass.assetTypeEnum = assetType;
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
    /**
     * Get the accountId field of this object.
     * @return A string representing the account ID of this Asset Object.
     */
    public String getAccountId() {
        return this.accountId;
    }

    /**
     * Get the projectId field of this object .
     * @return A string representing the project ID of this Asset Object.
     */
    public String getProjectId() {
        return this.projectId;
    }

    /**
     * Get the kind field of this object (the kind of Google Cloud Asset).
     * @return A string representing the kind of this Asset Object.
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Get the name field of this object.
     * @return A string representing the name of this Asset Object.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the id field of this object.
     * @return A string representing the id of this Asset Object.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get the type field of this object.
     * @return A string representing the type of this Asset Object.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get the location field of this object.
     * @return A string representing the location of this Asset Object.
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Get the creationTime field of this object.
     * @return A Timestamp object representing the creation time of this Asset Object.
     */
    public Timestamp getCreationTime() {
        return this.creationTime;
    }

    /**
     * Get the status field of this object.
     * @return A string representing the status of this Asset Object.
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Get the AssetType of this object.
     * @return An enum of AssetType representing the specific type of this Asset Object.
     */
    public AssetType getAssetTypeEnum() {
        return this.assetTypeEnum;
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
            String error_msg = "Encountered a casting error, expected to get an object that can be " +
                    "casted into a string. Received object: " + url;
            logger.atInfo().withCause(exception).log(error_msg);
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
            List<String> listToReturn = new ArrayList<>();
            for (String url : urlsList) {
                listToReturn.add(getLastSeg(url));
            }
            return listToReturn;
        } catch (ClassCastException exception) {
            String error_msg = "Encountered a casting error, expected to get an object that can be " +
                    "casted into a list of strings. Received object: " + urlsObject;
            logger.atInfo().withCause(exception).log(error_msg);
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
            String error_msg = "Encountered a date parsing error. Dates should be in " +
                                "yyyy-MM-ddTHH:mm:ss format, provided date: " + dateString;
            logger.atInfo().withCause(exception).log(error_msg);
        } catch (ClassCastException exception) {
            String error_msg = "Encountered a casting error, expected to get an object that can be " +
                    "casted into a string. Received object: " + dateString;
            logger.atInfo().withCause(exception).log(error_msg);
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
            String error_msg = "Encountered a casting error, expected to get an object that can be " +
                                "casted into a string. Received object: " + intString;
            logger.atInfo().withCause(exception).log(error_msg);
        } catch (NumberFormatException exception) {
            String error_msg = "Encountered a formatting error, expected to get an object that can be " +
                                "casted into an Integer. Received object: " + intString;
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return null;
    }

    /*
    This function receives an Object representing a string and returns it as a String.
    If the provided stringToConvert can not be casted into a String, null is returned and details
    are logged.
     */
    protected static String convertObjectToString(Object stringToConvert) {
        try {
            return (String) stringToConvert;
        } catch (ClassCastException exception) {
            String error_msg = "Encountered a casting error, expected to get an object that can be " +
                    "casted into a string. Received object: " + stringToConvert;
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return null;
    }

    /*
    This function receives an Object representing a boolean and returns it as a Boolean.
    If the provided booleanToConvert can not be casted into a Boolean, null is returned and details
    are logged.
     */
    protected static Boolean convertObjectToBoolean(Object booleanToConvert) {
        try {
            return (Boolean) booleanToConvert;
        } catch (ClassCastException exception) {
            String error_msg = "Encountered a casting error, expected to get an object that can be " +
                    "casted into a boolean. Received object: " + booleanToConvert;
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return null;
    }

    /*
    This function receives an Object and returns it as a HashMap<String, Object>.
    If the provided mapToConvert can not be casted into a  HashMap<String, Object>, null is returned
    and details are logged.
     */
    protected static HashMap<String, Object> convertObjectToMap(Object mapToConvert) {
        try {
            return (HashMap) mapToConvert;
        } catch (ClassCastException exception) {
            String error_msg = "Encountered a casting error, expected to get an object that can be " +
                    "casted into a HashMap<String, Object>. Received object: " + mapToConvert;
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return null;
    }
}