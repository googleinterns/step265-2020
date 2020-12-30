package com.google.cloudassets.acounts;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.IamScopes;
import com.google.api.services.iam.v1.model.CreateServiceAccountRequest;
import com.google.api.services.iam.v1.model.ServiceAccount;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * This Class creates a new ServiceAccount
 */
public class CreateServiceAccount {

    /**
     * This is used to create a new service account
     *
     * @param projectId   - Our projectID to add credentials
     * @param serviceAccountName - Use workspaceID and userName to create an account
     * @param workspaceName   - The chosen workspace nane by the user to add as name
     */
    public static ServiceAccount createServiceAccount(String projectId, String serviceAccountName, String workspaceName) {
        Iam service = null;
        try {
            service = initService();
        } catch (IOException | GeneralSecurityException e) {
            return null;
        }
        try {
            ServiceAccount serviceAccount = new ServiceAccount();
            serviceAccount.setDisplayName(workspaceName);
            CreateServiceAccountRequest request = new CreateServiceAccountRequest();
            request.setAccountId(serviceAccountName);
            request.setServiceAccount(serviceAccount);
            serviceAccount =
                    service.projects().serviceAccounts().create("projects/" + projectId, request).execute();
            return serviceAccount;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * This is used to initialize the IAM service, which can be used to send requests to the IAM API
     */
    private static Iam initService() throws GeneralSecurityException, IOException {
        // Use the Application Default Credentials strategy for authentication
        GoogleCredentials credential =
                GoogleCredentials.getApplicationDefault()
                        .createScoped(Collections.singleton(IamScopes.CLOUD_PLATFORM));
        Iam service =
                new Iam.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JacksonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credential))
                        .setApplicationName("service-accounts")
                        .build();
        return service;
    }
}