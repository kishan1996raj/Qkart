package QKART_TESTNG;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.net.MalformedURLException;
import java.net.URL;
 import QKART_TESTNG.QKART_Tests;

public class ListenerClass implements ITestListener {
    // public static RemoteWebDriver createDriver() throws MalformedURLException {
    //     // Launch Browser using Zalenium
    //     final DesiredCapabilities capabilities = new DesiredCapabilities();
    //     capabilities.setBrowserName(BrowserType.CHROME);
    //     RemoteWebDriver driver =
    //             new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);

    //     return driver;
    // }
   

    // public static void takeScreenshot(RemoteWebDriver driver, String description) {
    //     try {
    //         File theDir = new File("/screenshots");
    //         if (!theDir.exists()) {
    //             theDir.mkdirs();
    //         }
    //         String timestamp = String.valueOf(java.time.LocalDateTime.now());
    //         String fileName = String.format("screenshot_%s_%s.png", timestamp, description);
    //         TakesScreenshot scrShot = ((TakesScreenshot) driver);
    //         File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
    //         File DestFile = new File("screenshots/" + fileName);
    //         FileUtils.copyFile(SrcFile, DestFile);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //    
    public void onTestStart(ITestResult result) {
        System.out.println("Test Execution Started");
         QKART_Tests.takeScreenshot(QKART_Tests.driver, "ON_TEST_START", "LISTENERS");
        }

    

    public void onTestFailure(ITestResult result) {
        System.out.println("TestQ Failed");
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "ON_TEST_Failure", "LISTENERS");
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println("Test Passed");
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "ON_TEST_Success", "LISTENERS");
    }



}
