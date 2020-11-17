package resourceDisplay;

/**
 * This Class is used to get the wanted workspace from the user
 */
public class WorkspaceObject {
    private String workspaceID;
    private String chosenWorkspaceID = "--";

    public String getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(String workspaceID) {
        this.workspaceID = workspaceID;
    }

    public String getChosenWorkspaceID() {
        return chosenWorkspaceID;
    }

    public void setChosenWorkspaceID(String chosenWorkspaceID) {
        this.chosenWorkspaceID = chosenWorkspaceID;
    }
}