package resourceDisplay;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

import java.util.Date;

public class TestAsset {

    private String accountId;

    @Override
    public String toString() {
        return "TestAsset{" +
                "accountId='" + accountId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", zone='" + zone + '\'' +
                ", creationTime=" + creationTime +
                ", status='" + status + '\'' +
                '}';
    }

    private String projectId;
    protected String kind;
    protected String name;
    protected String id;
    protected String type;
    protected String zone;
    protected Timestamp creationTime;
    protected String status;

    public TestAsset(){}

    public TestAsset(String name, String type, String zone) {
        this.name = name;
        this.type = type;
        this.zone = zone;
    }

    public TestAsset(String accountId, String projectId, String kind, String name, String id, String type, String zone, Timestamp creationTime, String status) {
        this.accountId = accountId;
        this.projectId = projectId;
        this.kind = kind;
        this.name = name;
        this.id = id;
        this.type = type;
        this.zone = zone;
        this.creationTime = creationTime;
        this.status = status;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}