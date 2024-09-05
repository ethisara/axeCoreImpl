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
    public void verifyAllyTest() throws JSONException, InterruptedException {

        webdriverInit();

        String url1 = "https://www.w3.org/WAI/demos/bad/before/home.html";
        String url2 = "https://broken-workshop.dequelabs.com/";
        String url3 = "https://dequeuniversity.com/demo/dream";
        String url4 = "https://webtestingcourse.dequecloud.com/";
        String url5 = "https://dequeuniversity.com/demo/mars/";
        String url6 = "https://www.calstatela.edu/drupaltraining/web-accessibility-demo";

        navigationUrl = url6;

        driver.navigate().to(navigationUrl);

        if(navigationUrl == url1){
            pageName = "BeforeAndAfter";
        }else if(navigationUrl == url2){
            pageName = "BrokenWorkshop";
        }else if(navigationUrl == url3){
            pageName = "Dream";
        }else if(navigationUrl == url4){
            pageName = "WebTestingCourse";
        }else if(navigationUrl == url5){
            pageName = "Mars";
        }else if(navigationUrl == url6){
            pageName = "WebAccessibilityDemo";
        }

        System.out.println("Current URL: " + driver.getCurrentUrl());
        if(verifyAlly(pageName) == false){
            System.out.println("There are accessibility errors in : " + driver.getCurrentUrl());
        }else{
            System.out.println("There are no accessibility errors in : " + driver.getCurrentUrl());
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

    public static boolean verifyAlly(String page) throws JSONException, InterruptedException {

//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{runOnly:['wcag21aa']}").analyze();
//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2a', 'wcag2aa'] } }").analyze();
//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'] } }").analyze();
//        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2a'] } }").analyze();
        JSONObject responseJson = new AXE.Builder(driver, scriptUrl).options("{ runOnly: { type: 'tag', values: ['wcag2aa'] } }").analyze();
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