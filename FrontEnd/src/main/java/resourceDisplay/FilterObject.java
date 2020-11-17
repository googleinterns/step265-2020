package resourceDisplay;

/**
 * This Class is used to get all filters from user for allAssets
 */
public class FilterObject {

    private String location;
    private String status;
    private String kind;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}