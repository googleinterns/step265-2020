package resourceDisplay;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;

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

    /**
     * This is the url you get to when you log-in
     * @return the login template
     */
    @GetMapping("/")
    public String login() {
        return "login";
    }
    /**
     * This is the page you get to when after log-in that holds all different mappings
     * @return the index template
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    /**
     * This page returns all the assets in the DB
     * @param principal - Used to check authentication
     * @param model - Used to show table to user
     * @return the allassets template
     */
    @GetMapping("/allassets")
    public String getAll(@AuthenticationPrincipal OAuth2User principal, Model model) {
        SpannerOptions options = SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
            db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
            dbClient = spanner.getDatabaseClient(db);
        AssetsRepository assets = new AssetsRepository();
        List<String> displayNames = new ArrayList<>();
        List<List<String>> resultTable = assets.getAllAssets(dbClient, displayNames);
        model.addAttribute("displayNames", displayNames);
        model.addAttribute("allAssets", resultTable);
        return "allAssets";
    }

    // todo
    /**
     * This page returns assets by kind (with the specific data per asset)
     * @param principal - Used to check authentication
     * @param model - Used to show table to user
     * @return the bykind template
     */
    @GetMapping("/bykind")
    public String getByType(@AuthenticationPrincipal OAuth2User principal, Model model) {
        SpannerOptions options = SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
        // create spanner DatabaseClient
        db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        dbClient = spanner.getDatabaseClient(db);
        AssetsRepository assets = new AssetsRepository();
        List<String> displayNames = new ArrayList<>();
        List<TestAsset> resultTable = assets.getAssetByKind(dbClient, displayNames, "compute#disk");
        return "bykind";
    }
    /**
     * This page returns assets by status
     * @param principal - Used to check authentication
     * @param model - Used to show table to user
     * @param statusObject - Used to get filter from user
     * @return the bystatus template
     */
    @GetMapping("/bystatus")
    public String getByStatus(@AuthenticationPrincipal OAuth2User principal, Model model, @ModelAttribute StatusObject statusObject) {
        SpannerOptions options = SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
        db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        dbClient = spanner.getDatabaseClient(db);
        AssetsRepository assets = new AssetsRepository();
        model.addAttribute("statusObject", statusObject);
        /* This workspaceID is temporary until we add user/workspace/project table*/
        List<String> statusList = assets.getStatusList(dbClient, "noasan");
        model.addAttribute("statusList", statusList);
        List<String> displayNames = new ArrayList<>();
        List<List<String>> resultTable = assets.getAssetsByStatus(dbClient, displayNames, statusObject.getStatus());
        model.addAttribute("displayNames", displayNames);
        model.addAttribute("allAssets", resultTable);
        return "bystatus";
    }
    /**
     * This page returns assets by location
     * @param principal - Used to check authentication
     * @param model - Used to show table to user
     * @param locationObject - Used to get filter from user
     * @return the bylocation template
     */
    @GetMapping("/bylocation")
    public String getByLocation(@AuthenticationPrincipal OAuth2User principal, Model model, @ModelAttribute LocationObject locationObject) {
        SpannerOptions options = SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
        db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        dbClient = spanner.getDatabaseClient(db);
        AssetsRepository assets = new AssetsRepository();
        model.addAttribute("locationObject", locationObject);
        /* This workspaceID is temporary until we add user/workspace/project table*/
        List<String> locationList = assets.getLocationList(dbClient, "noasan");
        model.addAttribute("locationList", locationList);
        List<String> displayNames = new ArrayList<>();
        List<List<String>> resultTable = assets.getAssetsByLocation(dbClient, displayNames, locationObject.getLocation());
        model.addAttribute("displayNames", displayNames);
        model.addAttribute("allAssets", resultTable);
        return "bylocation";
    }

}
