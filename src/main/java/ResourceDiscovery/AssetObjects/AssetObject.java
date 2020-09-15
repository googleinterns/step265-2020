package ResourceDiscovery.AssetObjects;

import ResourceDiscovery.ProjectObjects.ProjectConfig;
import com.google.common.flogger.FluentLogger;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.NotMapped;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Table(name = "Assets")
abstract public class AssetObject {
    @PrimaryKey(keyOrder = 1)
    private String accountId = ProjectConfig.getInstance().getAccountId();
    @PrimaryKey(keyOrder = 2)
    private String projectId = ProjectConfig.getInstance().getProjectId();
    // We are using both the asset name and kind in order to ensure uniqueness
    @PrimaryKey(keyOrder = 4)
    protected String kind;
    @PrimaryKey(keyOrder = 3)
    @Column(name = "assetName")
    protected String name;

    @Column(name = "assetId")
    protected String id;
    @Column(name = "assetType")
    protected String type;
    protected String zone;
    protected Date creationTime;
    protected String status;

    @NotMapped
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    @NotMapped
    private static final Pattern LAST_SEGMENT_PATTERN = Pattern.compile(".*/");
    @NotMapped
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected abstract static class BaseBuilder<T extends AssetObject, B extends BaseBuilder> {
        protected Map<String,String> assetObjectsMap;
        protected T specificObjectClass;
        protected B specificObjectClassBuilder;

        protected abstract T getSpecificClass();
        protected abstract B getSpecificClassBuilder();

        /*
         * The constructor of the BaseBuilder class which set the relevant objects for the specific
         * object.
         * @param assetMap - a Map<String,String> of the relevant asset properties.
         */
        protected BaseBuilder(Map<String,String> assetMap) {
            specificObjectClass = getSpecificClass();
            specificObjectClassBuilder = getSpecificClassBuilder();
            assetObjectsMap = assetMap;
        }

        /*
        Set the kind field of this object with the provided string and return its specific Builder.
        */
        public B setKind(String kind) {
            specificObjectClass.kind = kind;
            return specificObjectClassBuilder;
        }

        /*
        Set the name field of this object with the provided string and return its specific Builder.
        */
        public B setName(String name) {
            specificObjectClass.name = name;
            return specificObjectClassBuilder;
        }

        /*
        Set the id field of this object with the provided string and return its specific Builder.
        */
        public B setId(String id) {
            specificObjectClass.id = id;
            return specificObjectClassBuilder;
        }

        /*
        Set the type field of this object with the provided string and return its specific Builder.
        */
        public B setType(String type) {
            specificObjectClass.type = type;
            return specificObjectClassBuilder;
        }

        /*
        Set the zone field of this object with the provided string and return its specific Builder.
        */
        public B setZone(String zone) {
            specificObjectClass.zone = zone;
            return specificObjectClassBuilder;
        }

        /*
        Set the creationTime field of this object with the provided string and return its specific Builder.
        */
        public B setCreationTime(Date creationTime) {
            specificObjectClass.creationTime = creationTime;
            return specificObjectClassBuilder;
        }

        /*
        Set the status field of this object with the provided string and return its specific Builder.
        */
        public B setStatus(String status) {
            specificObjectClass.status = status;
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
     * @return A Date object representing the creation time of this Asset Object.
     */
    public Date getCreationTime() {
        return this.creationTime;
    }

    /**
     * Get the status field of this object.
     * @return A string representing the status of this Asset Object.
     */
    public String getStatus() {
        return this.status;
    }

    /*
     * This function receives a string representing a url and extracts the string after the last '/'
     * char in the url.
     * If the provided string does not match that pattern the original string is returned.
     */
    protected static String getLastSeg(String url) {
        Matcher matcher = LAST_SEGMENT_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.replaceAll("");
        }
        return url;
    }

    /*
    This function receives a string representing a date and time and returns it as a Date object.
    The provided dateString should be in the following format: yyyy-MM-ddTHH:mm:ss
    If the provided dateString does not match this format null is returned and details are logged.
     */
    protected static Date convertStringToDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException exception) {
            String error_msg = "Encountered a date parsing error. Dates should be in " +
                                "yyyy-MM-ddTHH:mm:ss format, provided date: " + dateString;
            logger.atInfo().withCause(exception).log(error_msg);
        }
        return null;
    }
}