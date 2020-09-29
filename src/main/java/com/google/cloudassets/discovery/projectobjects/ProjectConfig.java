package com.google.cloudassets.discovery.projectobjects;

/**
 * The ProjectConfig class provides setters and getters for a certain project configurations.
 */
public class ProjectConfig {
    private static ProjectConfig projectConfigInstance = null;
    private String workspaceId;
    private String projectId;

    /**
     * This function returns the instance of the singleton ProjectConfig class.
     * @return a ProjectConfig object.
     */
    public static ProjectConfig getInstance() {
        if (projectConfigInstance == null) {
            projectConfigInstance = new ProjectConfig();
        }
        return projectConfigInstance;
    }

    /**
     * This setter function updates the workspaceId and projectId for the current project.
     * @param workspace - a string representing the workspace id.
     * @param project - a string representing the project id.
     */
    public void setNewProject(String workspace, String project) {
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
