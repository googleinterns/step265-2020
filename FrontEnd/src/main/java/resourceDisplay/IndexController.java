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
     *
     * @return the login template
     */
    @GetMapping("/")
    public String login() {
        return "login";
    }

    /**
     * This is the page you get to when after log-in that holds all different mappings
     *
     * @return the index template
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * This page returns all assets in the DB with different filtering options
     *
     * @param principal    - Used to check authentication
     * @param model        - Used to show table to user
     * @param filterObject - Used to get filters from the user
     * @return the bystatus template
     */
    @GetMapping("/allassets")
    public String test(@AuthenticationPrincipal OAuth2User principal, Model model,
                       @ModelAttribute FilterObject filterObject) {
        SpannerOptions options =
                SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
        db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        dbClient = spanner.getDatabaseClient(db);
        AssetsRepository assets = new AssetsRepository();
        model.addAttribute("filterObject", filterObject);
        //TODO use real workspaceID from user/workspace/project table this is temporary (using "noasan")
        List<String> locationList = assets.getFilterList(dbClient, "noasan", "location");
        model.addAttribute("locationList", locationList);
        List<String> statusList = assets.getFilterList(dbClient, "noasan", "status");
        model.addAttribute("statusList", statusList);
        List<String> kindList = assets.getFilterList(dbClient, "noasan", "kind");
        model.addAttribute("kindList", kindList);
        String status = filterObject.getStatus();
        String location = filterObject.getLocation();
        String kind = filterObject.getKind();
        if(status != null && location != null && kind != null) {
            ResultListObject resultListObject = assets.getAllAssets(dbClient, location, status, kind);
            model.addAttribute("displayNames", resultListObject.columnDisplays);
            model.addAttribute("allAssets", resultListObject.columnResults);
        }
        return "allassets";
    }

    /**
     * This page returns assets by kind (with the specific data per asset)
     *
     * @param principal - Used to check authentication
     * @param model     - Used to show table to user
     * @return the bykind template
     */
    @GetMapping("/bykind")
    public String getByKind(@AuthenticationPrincipal OAuth2User principal, Model model,
                            @ModelAttribute KindObject kindObject) {
        SpannerOptions options =
                SpannerOptions.newBuilder().setProjectId(SPANNER_PROJECT_ID).build();
        spanner = options.getService();
        db = DatabaseId.of(SPANNER_PROJECT_ID, SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        dbClient = spanner.getDatabaseClient(db);
        AssetsRepository assets = new AssetsRepository();
        model.addAttribute("kindObject", kindObject);
        //TODO use real workspaceID from user/workspace/project table this is temporary (using "noasan")
        List<String> kindList = assets.getFilterList(dbClient, "noasan", "kind");
        model.addAttribute("kindList", kindList);
        String kind = kindObject.getKind();
        if (kind != null) {
            ResultListObject resultListObject = assets.getAssetsByKind(dbClient, kind);
            model.addAttribute("displayNames", resultListObject.columnDisplays);
            model.addAttribute("allAssets", resultListObject.columnResults);
        }
        return "bykind";
    }
}
