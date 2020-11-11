package com.google.cloudassets.discovery.projectobjects;

/**
 * The ProjectConfig class provides setters and getters for a certain project configurations.
 */
public class ProjectConfig {
    private final String workspaceId;
    private final String projectId;
    private final String serviceAccountEmail;

    /**
     * This constructor updates the workspaceId and projectId for the current project.
     * @param workspace - a string representing the workspace id.
     * @param project - a string representing the project id.
     * @param serviceAccount - a string representing the service account that should be used to
     *                       retrieve data of this project.
     */
    public ProjectConfig(String workspace, String project, String serviceAccount) {
        this.workspaceId = workspace;
        this.projectId = project;
        this.serviceAccountEmail = serviceAccount;
    }

    /**
     * @return a string representing the workspace ID of this project.
     */
    public String getWorkspaceId() {
        return this.workspaceId;
    }

    /**
     * @return a string representing the project ID of this project.
     */
    public String getProjectId() {
        return this.projectId;
    }

    /**
     * @return a string representing the service account that should be used to retrieve data of
     * this project.
     */
    public String getServiceAccountEmail() {
        return this.serviceAccountEmail;
    }
}
