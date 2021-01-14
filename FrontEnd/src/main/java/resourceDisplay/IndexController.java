package resourceDisplay;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloudassets.acounts.CreateWorkspace;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

/**
 * This Class holds all the different mappings
 */
@Controller
public class IndexController {
    private static final String SPANNER_PROJECT_ID = "noa-yarden-2020";
    private static final String SPANNER_INSTANCE_ID = "spanner1";
    private static final String SPANNER_DATABASE_ID = "db1";
    private static Spanner spanner;
    private static DatabaseId db;
    private static DatabaseClient dbClient;
    private static String userID;
    private static String userName;
    private static WorkspaceObject chosenWorkspace;

    /**
     * This helper function returns a DatabaseClient to use for connection to DB
     *
     * @param projectID    - The project ID that the spanner instance is on
     * @param instanceID        - The spanner instance ID
     * @param dbID - The DB ID
     * @return A db client
     */
    private DatabaseClient getDbClient(String projectID, String instanceID, String dbID) {
        SpannerOptions options =
                SpannerOptions.newBuilder().setProjectId(projectID).build();
        spanner = options.getService();
        db = DatabaseId.of(projectID, instanceID, dbID);
        dbClient = spanner.getDatabaseClient(db);
        return dbClient;
    }

    /**
     * This is the url you get to when you log-in
     *
     * @return the login template
     */
    @GetMapping("/")
    public String login() {
        return "login";
    }

    /**
     * This is the page you get to when after log-in that holds all different mappings
     * @param principal    - Used to check authentication and save current user
     * @param model        - Used to show table to user
     * @param filterObject - Used to get filters from the user
     * @param workspaceObject - Holds the current chosen workspace
     * @return the index template
     */
    @GetMapping("/index")
    public String chooseWorkspace(@AuthenticationPrincipal OAuth2User principal, Model model,
                                  @ModelAttribute FilterObject filterObject, @ModelAttribute WorkspaceObject workspaceObject) {
        userID = principal.getAttribute("email");
        userName = principal.getAttribute("name");
        dbClient = getDbClient(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        AssetsRepository assets = new AssetsRepository();
        List<WorkspaceObject> workspaceIdList = assets.getWorkspaceIdList(dbClient, userID);
        model.addAttribute("workspaceIdList", workspaceIdList);
        //model.addAttribute("workspaceObject", workspaceObject);
        if(!workspaceIdList.isEmpty()) {
            chosenWorkspace = new WorkspaceObject(workspaceIdList.get(0).getWorkspaceID(), workspaceIdList.get(0).getWorkspaceDisplayName());
//            chosenWorkspace.setWorkspaceID(workspaceIdList.get(0).getWorkspaceID());
//            chosenWorkspace.setWorkspaceID(workspaceIdList.get(0).getWorkspaceDisplayName());
        }
        model.addAttribute("chosenWorkspace", chosenWorkspace);
        if (workspaceObject.getWorkspaceID() != null) {
            chosenWorkspace.setWorkspaceID(workspaceObject.getWorkspaceID());
            for (int i = 0; i < workspaceIdList.size(); i++) {
                if (workspaceIdList.get(i).getWorkspaceID().equals(workspaceObject.getWorkspaceID())) {
                    chosenWorkspace.setWorkspaceDisplayName(workspaceIdList.get(i).getWorkspaceDisplayName());
                    break;
                }
            }
        }
        return "index";
    }

    /**
     * This page returns all assets in the DB with different filtering options
     *
     * @param principal    - Used to check authentication
     * @param model        - Used to show table to user
     * @param filterObject - Used to get filters from the user
     * @param workspaceObject - Holds the current chosen workspace
     * @return the allassets template
     */
    @GetMapping("/allassets")
    public String getAll(@AuthenticationPrincipal OAuth2User principal, Model model,
                         @ModelAttribute FilterObject filterObject, @ModelAttribute WorkspaceObject workspaceObject) {
        dbClient = getDbClient(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        AssetsRepository assets = new AssetsRepository();
        List<WorkspaceObject> workspaceIdList = assets.getWorkspaceIdList(dbClient, userID);
        model.addAttribute("workspaceIdList", workspaceIdList);
        model.addAttribute("workspaceObject", workspaceObject);
        model.addAttribute("chosenWorkspace", chosenWorkspace);
        if (workspaceObject.getWorkspaceID() != null) {
            chosenWorkspace.setWorkspaceID(workspaceObject.getWorkspaceID());
            for (int i = 0; i < workspaceIdList.size(); i++) {
                if (workspaceIdList.get(i).getWorkspaceID().equals(workspaceObject.getWorkspaceID())) {
                    chosenWorkspace.setWorkspaceDisplayName(workspaceIdList.get(i).getWorkspaceDisplayName());
                    break;
                }
            }
        }
        if (chosenWorkspace.getWorkspaceID() != null) {
            model.addAttribute("filterObject", filterObject);
            List<String> locationList = assets.getFilterList(dbClient, chosenWorkspace.getWorkspaceID() , "location");
            model.addAttribute("locationList", locationList);
            List<String> statusList = assets.getFilterList(dbClient, chosenWorkspace.getWorkspaceID() , "status");
            model.addAttribute("statusList", statusList);
            List<String> kindList = assets.getFilterList(dbClient, chosenWorkspace.getWorkspaceID() , "kind");
            model.addAttribute("kindList", kindList);
            String status = filterObject.getStatus();
            String location = filterObject.getLocation();
            String kind = filterObject.getKind();
            if (status != null && location != null && kind != null) {
                ResultListObject resultListObject = assets.getAllAssets(dbClient, location, status, kind, chosenWorkspace.getWorkspaceID() );
                model.addAttribute("displayNames", resultListObject.columnDisplays);
                model.addAttribute("allAssets", resultListObject.columnResults);
            }
        }
        return "allassets";
    }

    /**
     * This page returns assets by kind (with the specific data per asset)
     *
     * @param principal    - Used to check authentication
     * @param model        - Used to show table to user
     * @param kindObject - Used to get kind filter from the user
     * @param workspaceObject - Holds the current chosen workspace
     * @return the bykind template
     */
    @GetMapping("/bykind")
    public String getByKind(@AuthenticationPrincipal OAuth2User principal, Model model,
                            @ModelAttribute KindObject kindObject, @ModelAttribute WorkspaceObject workspaceObject) {
        dbClient = getDbClient(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        AssetsRepository assets = new AssetsRepository();
        List<WorkspaceObject> workspaceIdList = assets.getWorkspaceIdList(dbClient, userID);
        model.addAttribute("workspaceIdList", workspaceIdList);
        model.addAttribute("workspaceObject", workspaceObject);
        model.addAttribute("chosenWorkspace", chosenWorkspace);
        if (workspaceObject.getWorkspaceID() != null) {
            chosenWorkspace.setWorkspaceID(workspaceObject.getWorkspaceID());
            for (int i = 0; i < workspaceIdList.size(); i++) {
                if (workspaceIdList.get(i).getWorkspaceID().equals(workspaceObject.getWorkspaceID())) {
                    chosenWorkspace.setWorkspaceDisplayName(workspaceIdList.get(i).getWorkspaceDisplayName());
                    break;
                }
            }
        }
        model.addAttribute("kindObject", kindObject);
        List<String> kindList = assets.getFilterList(dbClient, chosenWorkspace.getWorkspaceID(), "kind");
        model.addAttribute("kindList", kindList);
        String kind = kindObject.getKind();
        if (kind != null && !kind.equals("")) {
            ResultListObject resultListObject = assets.getAssetsByKind(dbClient, kind, chosenWorkspace.getWorkspaceID());
            model.addAttribute("displayNames", resultListObject.columnDisplays);
            model.addAttribute("allAssets", resultListObject.columnResults);
        }
        return "bykind";
    }

    /**
     * This page is used to create a new workspace
     *
     * @param principal    - Used to check authentication
     * @param model        - Used to show table to user
     * @param createWorkspace - Used to create a new workspace
     * @param workspaceObject - Holds the current chosen workspace
     * @return the newworkspace template
     */
    @GetMapping("/newworkspace")
    public String createNewWorkspace(@AuthenticationPrincipal OAuth2User principal, Model model,
                                     @ModelAttribute CreateWorkspace createWorkspace, @ModelAttribute WorkspaceObject workspaceObject) {
        dbClient = getDbClient(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        createWorkspace.setUserName(userName);
        AssetsRepository assets = new AssetsRepository();
        List<WorkspaceObject> workspaceIdList = assets.getWorkspaceIdList(dbClient, userID);
        model.addAttribute("workspaceIdList", workspaceIdList);
        model.addAttribute("createWorkspace", createWorkspace);
        model.addAttribute("serviceAccount", createWorkspace.getServiceAccount());
        model.addAttribute("workspaceObject", workspaceObject);
        model.addAttribute("chosenWorkspace", chosenWorkspace);
        if (workspaceObject.getWorkspaceID() != null) {
            chosenWorkspace.setWorkspaceID(workspaceObject.getWorkspaceID());
            for (int i = 0; i < workspaceIdList.size(); i++) {
                if (workspaceIdList.get(i).getWorkspaceID().equals(workspaceObject.getWorkspaceID())) {
                    chosenWorkspace.setWorkspaceDisplayName(workspaceIdList.get(i).getWorkspaceDisplayName());
                    break;
                }
            }
        }
        return "newworkspace";
    }

    /**
     * This page shows a newly created workspace
     *
     * @param principal    - Used to check authentication
     * @param model        - Used to show table to user
     * @param createWorkspace - Shows newly created workspace
     * @param workspaceObject - Holds the current chosen workspace
     * @return the showworkspace template
     */
    @PostMapping("/newworkspace")
    public String showNewWorkspace(@AuthenticationPrincipal OAuth2User principal, Model model,
                                   @ModelAttribute CreateWorkspace createWorkspace, @ModelAttribute WorkspaceObject workspaceObject) {
        dbClient = getDbClient(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        AssetsRepository assets = new AssetsRepository();
        List<WorkspaceObject> workspaceIdList = assets.getWorkspaceIdList(dbClient, userID);
        model.addAttribute("workspaceIdList", workspaceIdList);
        model.addAttribute("workspaceObject", workspaceObject);
        model.addAttribute("chosenWorkspace", chosenWorkspace);
        if (workspaceObject.getWorkspaceID() != null) {
            chosenWorkspace.setWorkspaceID(workspaceObject.getWorkspaceID());
            for (int i = 0; i < workspaceIdList.size(); i++) {
                if (workspaceIdList.get(i).getWorkspaceID().equals(workspaceObject.getWorkspaceID())) {
                    chosenWorkspace.setWorkspaceDisplayName(workspaceIdList.get(i).getWorkspaceDisplayName());
                    break;
                }
            }
        }
        if (createWorkspace.getWorkspaceName() != null) {
            createWorkspace.setIdAndServiceAccount(userID, dbClient, userName);
        }
        return "showworkspace";
    }

}