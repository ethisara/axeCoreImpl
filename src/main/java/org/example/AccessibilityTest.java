package org.example;

import com.deque.axe.AXE;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccessibilityTest {
    public static WebDriver driver = null;
    private static boolean passStatus = false;
    private static final Logger logger = LoggerFactory.getLogger(AccessibilityTest.class);
    private static final URL scriptUrl = AccessibilityTest.class.getResource("/axe.min.js");
    public static String navigationUrl;
    public static String pageName;
    public static String tag;
    public static String tagValue;
    public static String targetFolderFilePath = System.getProperty("user.dir") + "/target/";

    public static  List<String> urls = Arrays.asList();


    public static void setUrls(){
        urls = Arrays.asList(
//                "https://www.w3.org/WAI/demos/bad/before/home.html",
//                "https://www.w3.org/WAI/demos/bad/before/news.html",
//                "https://www.w3.org/WAI/demos/bad/before/tickets.html",
//                "https://www.w3.org/WAI/demos/bad/before/survey.html",
//                "https://www.w3.org/WAI/demos/bad/before/template.html",
                "https://broken-workshop.dequelabs.com/",
                "https://dequeuniversity.com/demo/dream",
                "https://webtestingcourse.dequecloud.com/",
                "https://dequeuniversity.com/demo/mars/",
                "https://www.calstatela.edu/drupaltraining/web-accessibility-demo"
//                "https://www.iflysouthern.com/",
//                "https://nymag.com/",
//                "https://www.cbsnews.com/miami/"
        );
    }
    @Test
    public void verifyAllUrlsForListOfTags() throws JSONException, InterruptedException {

        webdriverInit();
        setUrls();

        List<String> wcag20tags = Arrays.asList("wcag244", "wcag412", "wcag111", "wcag131", "wcag222", "wcag241", "wcag143", "wcag242", "wcag332", "wcag312", "wcag122", "wcag135", "wcag1412", "wcag258");
        List<String> wcag21tags = Arrays.asList("wcag135", "wcag1412");
        List<String> wcag22tags = Arrays.asList("wcag258");
        List<String> bestPracticestags = Arrays.asList("best-practice","cat.aria","cat.name-role-value","cat.structure","review-item","cat.text-alternatives");

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(wcag20tags);
        combinedList.addAll(wcag21tags);
        combinedList.addAll(wcag22tags);
//        combinedList.addAll(bestPracticestags);

        for (String url : urls) {
            navigationUrl = url;
            driver.navigate().to(navigationUrl);
            for (String tag : combinedList) {
                this.tag = tag;

                // Get the page name for the report
                pageName = getPageNameFromUrl(navigationUrl);

                // Accessibility verification
                if (!verifyAllyForListOfTags(pageName, this.tag)) {
                    System.out.println("There are " +tagValue+ " accessibility errors in :" + driver.getCurrentUrl() +"\n");
                }
            }
        }
        driver.quit();
    }

    @Test
    public void verifyAllUrlsForSelectedTags() throws JSONException, InterruptedException {

        webdriverInit();
        setUrls();
        List<String> tags = Arrays.asList("wcag2aa","wcag2a","wcag21a","wcag21aa","wcag22aa");
        String tagsString = String.join("', '", tags);
        tagsString = "'" + tagsString + "'";

        for (String url : urls) {
            navigationUrl = url;
            driver.navigate().to(navigationUrl);

            for (String tag : tags) {
                this.tag = tagsString;

                // Get the page name for the report
                pageName = getPageNameFromUrl(navigationUrl);

                // Accessibikity verification
                if (!verifyAllyForSelectedTags(pageName, this.tag)) {
                    System.out.println("There are " +tagValue+ " accessibility errors in :" + driver.getCurrentUrl() +"\n");
                }
            }
        }
        driver.quit();
    }

    public static void webdriverInit(){
        ChromeOptions options = new ChromeOptions();
        options.setCapability("acceptInsecureCerts",true);
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "//src/test/resources/chromedriver.exe");
        driver = new ChromeDriver(options);
    }

    // Accessibility verification for all tags
    public static boolean verifyAllyForListOfTags(String page,String tag) throws JSONException, InterruptedException {

        // Implementation of the tag value
        String options = "{ runOnly: { type: 'tag', values: ['"+tag+"'] } }";
        JSONObject optionsJson = new JSONObject(options);
        tagValue = optionsJson.getJSONObject("runOnly").getJSONArray("values").getString(0);

        // Accessibility verification
        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options(options).analyze();
        JSONArray violations = responseJson.getJSONArray("violations");

        if (violations.length() == 0 ) {
            logger.info("There are no "+tagValue+" accessibility errors");
            passStatus = true;
        } else {
            logger.error("******* VIOLATIONS *******");
            AXE.writeResults(targetFolderFilePath + tagValue + " " + page + "allyTestReport", violations);
            axeJsonToHtml(violations,page);
            Thread.sleep(6000);
            passStatus = false;
            for (int i = 0; i < violations.length(); i++) {
                JSONObject violation = violations.getJSONObject(i);
                System.out.println((i + 1) + ". " + violation.getString("description"));
            }
        }
        return passStatus;
    }

    // Accessibility verification for selected tags
    public static boolean verifyAllyForSelectedTags(String page,String tag) throws JSONException, InterruptedException {

        // Implementation of the tag value
        String options = "{ runOnly: { type: 'tag', values: ["+tag+"] } }";
        JSONObject optionsJson = new JSONObject(options);
        tagValue = optionsJson.getJSONObject("runOnly").getJSONArray("values").getString(0);

        // Accessibility verification
        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options(options).analyze();
        JSONArray violations = responseJson.getJSONArray("violations");

        if (violations.length() == 0 ) {
            logger.info("There are no "+tagValue+" accessibility errors");
            passStatus = true;
        } else {
            logger.error("******* VIOLATIONS *******");
            AXE.writeResults(targetFolderFilePath + tagValue + " " + page + "allyTestReport", violations);
            axeJsonToHtml(violations,page);
            Thread.sleep(6000);
            passStatus = false;
            for (int i = 0; i < violations.length(); i++) {
                JSONObject violation = violations.getJSONObject(i);
                System.out.println((i + 1) + ". " + violation.getString("description"));
            }
        }
        return passStatus;
    }

    // Convert JSON to HTML report
    public static void axeJsonToHtml(JSONArray violations,String page){
        try (FileWriter file = new FileWriter(targetFolderFilePath+tagValue+" "+page+"allyTestReport.html")) {
            file.write("<html><head><title>Accessibility Report</title></head><body>");
            file.write("<h1>Accessibility Violations</h1>");
            for (int i = 0; i < violations.length(); i++) {
                JSONObject violation = violations.getJSONObject(i);
                file.write("<h2>" + violation.getString("description") + "</h2>");
                file.write("<p>" + violation.getString("help") + "</p>");
                file.write("<ul>");
                JSONArray nodes = violation.getJSONArray("nodes");
                for (int j = 0; j < nodes.length(); j++) {
                    JSONObject node = nodes.getJSONObject(j);
                    file.write("<li>" + node.getString("html") + "</li>");
                }
                file.write("</ul>");
            }
            file.write("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Setting the page name from the URL
    private String getPageNameFromUrl(String url) {
        Map<String, String> urlToPageName = new HashMap<>();
        urlToPageName.put("before/home.html", "home ");
        urlToPageName.put("broken-workshop.dequelabs.com", "Broken Workshop ");
        urlToPageName.put("dequeuniversity.com/demo/dream", "Dream ");
        urlToPageName.put("webtestingcourse.dequecloud.com", "Web Testing Course ");
        urlToPageName.put("dequeuniversity.com/demo/mars", "Mars ");
        urlToPageName.put("calstatela.edu/drupaltraining/web-accessibility-demo", "Web Accessibility Demo ");
        urlToPageName.put("iflysouthern.com", "Southern ");
        urlToPageName.put("nymag.com", "ny mag ");
        urlToPageName.put("before/news.html", "news ");
        urlToPageName.put("before/tickets.html", "tickets ");
        urlToPageName.put("before/survey.html", "survey ");
        urlToPageName.put("before/template.html", "template ");

        for (Map.Entry<String, String> entry : urlToPageName.entrySet()) {
            if (url.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "Unknown";

    }

}