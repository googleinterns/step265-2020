package com.google.cloudassets.discovery.projectobjects;

/**
 * The ProjectConfig class provides setters and getters for a certain project configurations.
 */
public class ProjectConfig {
    private static ProjectConfig projectConfigInstance = null;
    private String accountId;
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
     * This setter function updates the accountId and projectId for the current project.
     * @param account - a string representing the account id.
     * @param project - a string representing the project id.
     */
    public void setNewProject(String account, String project) {
        this.accountId = account;
        this.projectId = project;
    }

    /**
     * This getter function returns the accountId for the current project.
     * @return a string representing the account id.
     */
    public String getAccountId() {
        return this.accountId;
    }

    /**
     * This getter function returns the projectId for the current project.
     * @return a string representing the project id.
     */
    public String getProjectId() {
        return this.projectId;
    }
}
