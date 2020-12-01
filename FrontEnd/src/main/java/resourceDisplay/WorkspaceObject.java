package resourceDisplay;

/**
 * This Class is used to get the wanted workspace from the user and from the DB
 */
public class WorkspaceObject {
    private String workspaceID;
    private String workspaceDisplayName;

    public String getWorkspaceDisplayName() {
        return workspaceDisplayName;
    }

    public void setWorkspaceDisplayName(String workspaceDisplayName) {
        this.workspaceDisplayName = workspaceDisplayName;
    }

    public String getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(String workspaceID) {
        this.workspaceID = workspaceID;
    }

}