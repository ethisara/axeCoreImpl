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
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertTrue;


public class AccessibilityTest {
    public static WebDriver driver = null;
    private static boolean passStatus = true;
    private static final Logger logger = LoggerFactory.getLogger(AccessibilityTest.class);
    private static final URL scriptUrl = AccessibilityTest.class.getResource("/axe.min.js");
    public static String navigationUrl;
    public static String pageName;
    public static String tag;
    public static String tagValue;
    public static String targetFolderFilePath = System.getProperty("user.dir") + "/target/";


    @Test
    public void verifyAllUrls() throws JSONException, InterruptedException {

        webdriverInit();
//        List<String> tags = Arrays.asList(
//                "wcag2a",
//                "wcag2aa",
//                "wcag21a",
//                "wcag21aa",
//                "wcag22a",
//                "wcag22aa"
//        );

        List<String> tags = Arrays.asList("wcag247", "wcag2411", "wcag257", "wcag326", "wcag337", "wcag339", "wcag134", "wcag135", "wcag1410", "wcag1411", "wcag1412", "wcag214", "wcag251", "wcag254", "wcag256", "wcag111", "wcag124", "wcag125", "wcag131", "wcag132", "wcag133", "wcag143", "wcag144", "wcag145", "wcag211", "wcag212", "wcag243", "wcag244", "wcag246", "wcag247", "wcag312", "wcag323", "wcag324", "wcag333", "wcag334");

        List<String> urls = Arrays.asList(
                "https://www.w3.org/WAI/demos/bad/before/home.html",
                "https://broken-workshop.dequelabs.com/",
                "https://dequeuniversity.com/demo/dream",
                "https://webtestingcourse.dequecloud.com/",
                "https://dequeuniversity.com/demo/mars/",
                "https://www.calstatela.edu/drupaltraining/web-accessibility-demo",
                "https://www.iflysouthern.com/"
//                "https://nymag.com/",
//                "https://www.cbsnews.com/miami/",
//                "https://www.cbsnews.com/miami/"
        );

        for (String tag : tags) {
            this.tag = tag;
            for (String url : urls) {

                navigationUrl = url;
                driver.navigate().to(navigationUrl);

                // Get the page name for the report
                pageName = getPageNameFromUrl(navigationUrl);

                // Accessibikity verification
                if (!verifyAlly(pageName, this.tag)) {
                    System.out.println("There are "+tagValue+" accessibility errors :" + driver.getCurrentUrl());
                } else {
                    System.out.println("There are no "+tagValue+" accessibility errors :" + driver.getCurrentUrl());
                }
            }
        }
        driver.quit();
    }

    // Geting the page name from the URL
    private String getPageNameFromUrl(String url) {
        if (url.contains("before/home.html")) {
            return "BeforeAndAfter";
        } else if (url.contains("broken-workshop.dequelabs.com")) {
            return "BrokenWorkshop";
        } else if (url.contains("dequeuniversity.com/demo/dream")) {
            return "Dream";
        } else if (url.contains("webtestingcourse.dequecloud.com")) {
            return "WebTestingCourse";
        } else if (url.contains("dequeuniversity.com/demo/mars")) {
            return "Mars";
        } else if (url.contains("calstatela.edu/drupaltraining/web-accessibility-demo")) {
            return "WebAccessibilityDemo";
        } else if (url.contains("iflysouthern.com")) {
            return "Southern";
        } else {
            return "Unknown";
        }
    }
    public static void webdriverInit(){
        ChromeOptions options = new ChromeOptions();
        options.setCapability("acceptInsecureCerts",true);
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "//src/test/resources/chromedriver.exe");
        driver = new ChromeDriver(options);
    }

    // Accessibility verification
    public static boolean verifyAlly(String page,String tag) throws JSONException, InterruptedException {

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
            AXE.writeResults(targetFolderFilePath + tagValue + page + "allyTestReport", violations);
            axeJsonToHtml(violations,page);
            Thread.sleep(6000);
            passStatus = false;
        }
        return passStatus;
    }

    // Convert JSON to HTML report
    public static void axeJsonToHtml(JSONArray violations,String page){
        try (FileWriter file = new FileWriter(targetFolderFilePath+tagValue+page+"allyTestReport.html")) {
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


}