package com.google.cloudassets.acounts;

import com.google.api.services.iam.v1.model.ServiceAccount;
import com.google.cloud.spanner.DatabaseClient;
import resourceDisplay.AssetsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This Class creates a new Workspace
 */
public class CreateWorkspace {
    private String workspaceId;
    //todo add the check if the input is 6-30 small letters
    private String workspaceName;
    //todo: should change this to list of projects
    private String project;
    private List<String> projects;
    private ServiceAccount serviceAccount;
    private String serviceAccountEmail;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServiceAccountEmail() {
        return serviceAccountEmail;
    }

    public void setServiceAccountEmail(String serviceAccountEmail) {
        this.serviceAccountEmail = serviceAccountEmail;
    }

    /**
     * This is used to create a new workspace and service account and add to all relevant tables
     *
     * @param userId   - The new workspace Id to be created
     * @param dbClient - A client for connection to the DB
     * @param userName   - The user name to add to workspaceID
     */
    public void setIdAndServiceAccount(String userId, DatabaseClient dbClient, String userName) {
        this.workspaceId = this.workspaceName;
        String editedName = userName.replaceAll("\\s+","");
        this.workspaceId += "-" + editedName.toLowerCase();
        boolean isExists = AssetsRepository.checkIfWorkspaceExists(dbClient, workspaceId);
        if(!isExists) {
            this.serviceAccount = CreateServiceAccount.createServiceAccount("noa-yarden-2020", workspaceId, workspaceName);
            if (this.serviceAccount != null) {
                this.serviceAccountEmail = serviceAccount.getEmail();
                AssetsRepository.addNewWorkspaceAndAccount(dbClient, userId, workspaceName, workspaceId, projects, serviceAccountEmail);
            }
        }
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        projects = Arrays.asList(project.split(","));
    }

    public List<String> getProjects() {
        return projects;
    }

//    public void setProjects(String project) {
//        projects.add(project);
//    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public ServiceAccount getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(ServiceAccount serviceAccount) {
        this.serviceAccount = serviceAccount;
    }
}
