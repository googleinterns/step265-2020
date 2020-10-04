package com.google.cloudassets.discovery.projectobjects;

/**
 * The ProjectConfig class provides setters and getters for a certain project configurations.
 */
public class ProjectConfig {
    private final String workspaceId;
    private final String projectId;

    /**
     * This constructor updates the workspaceId and projectId for the current project.
     * @param workspace - a string representing the workspace id.
     * @param project - a string representing the project id.
     */
    public ProjectConfig(String workspace, String project) {
        this.workspaceId = workspace;
        this.projectId = project;
    }

    /**
     * This getter function returns the workspaceId for the current project.
     * @return a string representing the workspace id.
     */
    public String getWorkspaceId() {
        return this.workspaceId;
    }

    /**
     * This getter function returns the projectId for the current project.
     * @return a string representing the project id.
     */
    public String getProjectId() {
        return this.projectId;
    }
}
