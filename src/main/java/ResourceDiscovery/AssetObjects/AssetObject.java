package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.AssetTypes;
import ResourceDiscovery.ProjectObjects.ProjectConfig;
import com.google.cloud.Timestamp;
import com.google.common.flogger.FluentLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    // asset primary keys
    public String accountId = ProjectConfig.getInstance().getAccountId();
    public String projectId = ProjectConfig.getInstance().getProjectId();
    protected String kind;
    protected String name;

    // asset additional data
    protected String id;
    protected String type;
    protected String zone;
    protected Timestamp creationTime;
    protected String status;

    protected AssetTypes assetTypeEnum;

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static final Pattern LAST_SEGMENT_PATTERN = Pattern.compile(".*/");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected abstract static class BaseBuilder<T extends AssetObject, B extends BaseBuilder> {
        protected Map<String,Object> assetObjectsMap;
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
            assetObjectsMap = assetMap;
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
        Set the zone field of this object with the provided string and return its specific Builder.
        */
        public B setZone(Object zone) {
            specificObjectClass.zone = (String) zone;
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
        public B setAssetTypeEnum(AssetTypes assetType) {
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
     * Get the zone field of this object.
     * @return A string representing the zone of this Asset Object.
     */
    public String getZone() {
        return this.zone;
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
     * @return A enum of AssetTypes representing the specific type of this Asset Object.
     */
    public AssetTypes getAssetTypeEnum() {
        return this.assetTypeEnum;
    }

    /*
     * This function receives a string representing a url and extracts the string after the last '/'
     * char in the url.
     * If the provided string does not match that pattern the original string is returned.
     */
    protected static String getLastSeg(Object url) {
        String urlToReturn = (String) url;
        Matcher matcher = LAST_SEGMENT_PATTERN.matcher(urlToReturn);
        if (matcher.find()) {
            return matcher.replaceAll("");
        }
        return urlToReturn;
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
        }
        return null;
    }

    /*
    This function receives a string representing an int and returns it as an int.
     */
    protected static int convertStringToInt(Object intString) {
        return Integer.valueOf((String) intString);
    }
}