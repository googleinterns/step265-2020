package ResourceDiscovery;

import ResourceDiscovery.ProjectObjects.ProjectAssetsUpdater;
import ResourceDiscovery.ProjectObjects.ProjectConfig;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
@RestController
public class Main implements CommandLineRunner {
    private static final String PROJECTS_ID_FILEPATH = "src/main/resources/ProjectIds.txt";

    private static final JSONParser jsonParser = new JSONParser();
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Autowired
    SpannerSchemaTools spannerSchemaTools;

    @Autowired
    ProjectAssetsUpdater projectAssets;

    /**
     * This function maps the /requests url and initializes the backend process.
     * @return an empty string to be presented in the /requests url.
     */
    @GetMapping("/requests")
    public String runService() {
        run();
        return "";
    }

    /**
     * This is the main function which is in charge of running the SpringApplication.
     * @param args - no args needed.
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * This function initializes all of the mapping and updating of all of the assets for each
     * of the projects in the ProjectId.txt file.
     * @param args - no args needed.
     */
    public void run(String... args) {
        spannerSchemaTools.createTableIfNotExists();

        try {
            JSONObject projectJson = (JSONObject) jsonParser.parse(new FileReader(PROJECTS_ID_FILEPATH));
            for (Object key : projectJson.keySet()) {
                String accountId = (String) key;
                String projectId = (String) projectJson.get(key);

                ProjectConfig.getInstance().setNewProject(accountId, projectId);

                projectAssets.updateAssets();
            }
        } catch (ParseException exception) {
            String error_msg = "Encountered an ParseException while parsing " + PROJECTS_ID_FILEPATH;
            logger.atInfo().withCause(exception).log(error_msg);
        } catch (IOException exception) {
            String error_msg = "Encountered an IOException while reading " + PROJECTS_ID_FILEPATH;
            logger.atInfo().withCause(exception).log(error_msg);
        }
    }
}
