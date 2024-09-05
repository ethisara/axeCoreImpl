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
    public static String targetFolderFilePath = System.getProperty("user.dir") + "/target/";


    @Test
    public void verifyAllUrls() throws JSONException, InterruptedException {
        webdriverInit();
        List<String> urls = Arrays.asList(
                "https://www.w3.org/WAI/demos/bad/before/home.html",
                "https://broken-workshop.dequelabs.com/",
                "https://dequeuniversity.com/demo/dream",
                "https://webtestingcourse.dequecloud.com/",
                "https://dequeuniversity.com/demo/mars/",
                "https://www.calstatela.edu/drupaltraining/web-accessibility-demo",
                "https://www.iflysouthern.com/",
                "https://www.jacaranda.com.au/shop/"
        );

        for (String url : urls) {
            navigationUrl = url;
            driver.navigate().to(navigationUrl);
            pageName = getPageNameFromUrl(navigationUrl);
            System.out.println("Current URL: " + driver.getCurrentUrl());
            if (!verifyAlly(pageName)) {
                System.out.println("There are accessibility errors in : " + driver.getCurrentUrl());
            } else {
                System.out.println("There are no accessibility errors in : " + driver.getCurrentUrl());
            }
        }
        driver.quit();
    }

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
        } else if (url.contains("jacaranda.com.au/shop")) {
            return "JPQA";
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

    public static boolean verifyAlly(String page) throws JSONException, InterruptedException {

//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{runOnly:['wcag21aa']}").analyze();
//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2a', 'wcag2aa'] } }").analyze();
//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'] } }").analyze();
//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2a'] } }").analyze();
        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2a'] } }").analyze();
//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).analyze();

        JSONArray violations = responseJson.getJSONArray("violations");
        if (violations.length() == 0 ) {
            logger.info("There are no accessibility errors");
            passStatus = true;
        } else {
            logger.error("******* VIOLATIONS *******");
            AXE.writeResults(targetFolderFilePath + page + "allyTestReport", violations);
            axeJsonToHtml(violations,page);
            Thread.sleep(6000);
            passStatus = false;
        }
        return passStatus;
    }

    public static void axeJsonToHtml(JSONArray violations,String page){
        try (FileWriter file = new FileWriter(targetFolderFilePath+page+"allyTestReport.html")) {
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