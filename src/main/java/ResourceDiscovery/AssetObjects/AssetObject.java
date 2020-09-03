package ResourceDiscovery.AssetObjects;

import com.google.common.flogger.FluentLogger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class AssetObject {
    protected String kind;
    protected String name;
    protected String id;
    protected String type;
    protected String zone;
    protected Date creationTime;
    protected String status;

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final Pattern LAST_SEGMENT_PATTERN = Pattern.compile(".*/");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * This function is in charge of setting all of the AssetObject relevant fields from the given
     * map.
     * @param itemsMap - a Map<String,String> which contains the attributes of the AssetObject that
     *                   should be mapped into its fields.
     */
    abstract public void setFieldValues(Map<String,String> itemsMap);

    /*
     * This function receives a string representing a url and extracts the string after the last '/'
     * char in the url.
     * If the provided string does not match that pattern the original string is returned.
     */
    protected String getLastSeg(String url) {
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
    protected Date convertStringToDate(String dateString) {
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
