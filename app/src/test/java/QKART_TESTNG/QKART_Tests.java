package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout; 
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

     @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }
@BeforeTest
public void SynchingTest(){
    driver.manage().timeouts().implicitlyWait(5000, TimeUnit.SECONDS);
}
    /*
     * Testcase01: Verify a new user can successfully register
     */
         @Test(description = "Verify registration happens correctly", priority = 1, groups = {"Sanity"})
         @Parameters({"TC1_username","TC1_password"})
         public void TestCase01(@Optional("No input")String username, @Optional("No input")String password) throws InterruptedException {
        Boolean status;
         logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
         takeScreenshot(driver, "StartTestCase", "TestCase1");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
                registration.navigateToRegisterPage();
         status = registration.registerUser(username,password, true);
       Assert.assertTrue("Registraion failed", status);

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
         status = login.PerformLogin(lastGeneratedUserName, password);
         Thread.sleep(2000);
         logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        Assert.assertTrue("Failed to login with registered user",status);

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        
    }


    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   @Test(description = "Verify User Registration with an existing username", priority = 2, groups = {"Sanity"})
    public void TestCase02() throws InterruptedException {
        Boolean status;
        logStatus("Start Testcase", "Test Case 2: Verify User Registration with an existing username ", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
        Assert.assertTrue( "user registration failed",status);
        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
       Assert.assertFalse( "User regisration passed",status);
    }
    @Test(description = "Verify functionality of search box", priority = 3, groups = {"Sanity"})
    public void TestCase03() throws InterruptedException {
        logStatus("TestCase 3", "Start tlest case : Vero inify functionality of search box ", "DONE");
        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct("YONEX");
        Assert.assertTrue( "Unable to search product in the search box",status);

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
       Assert.assertTrue( "No Products found",searchResults.size() != 0);
        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
           Assert.assertTrue( "Test Case Failure. Test Results contains un-expected values: " +elementText+
            "FAIL",elementText.toUpperCase().contains("YONEX"));
           
           
        }

        logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        Assert.assertFalse( "Test Case Failure: Invalid keyword returned results",status);
        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        if (searchResults.size() == 0) {
          
               Assert.assertTrue("Test Case Fail. Expected: no results , actual: Results were available",(homePage.isNoResultFound()));
            
            logStatus("TestCase 3", "Test Case PASS. Verified that no search results were found for the given text",
                    "PASS");
        } 

       
    }
    @Test(description = "Verify the presence of size Chart", priority = 4, groups = {"Regression"})
    public void TestCase04() throws InterruptedException {
        logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
                Assert.assertTrue("Validation for  size chart link failed for the product "+result.getTitleofResult(), result.verifySizeChartExists());
                System.out.println("Step success : Successfully validated presence of Size Chart Link"); 
                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
                Assert.assertTrue("Validation for presence of dropdopdown failed for the product "+result.getTitleofResult(), status);
                System.out.println("Step Success : Validated presence of drop down for the product "+result.getTitleofResult());

                // Open the size chart
                Assert.assertTrue("Step Failed: Size Chart is not openeing for the product "+result.getTitleofResult(), result.openSizechart());
                    // Verify if the size chart contents matches the expected values
                Assert.assertTrue("Step failed:  Validation for size chart content failed for the product "+result.getTitleofResult(),result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver));
                        System.out.println("Step Success Successfully validated contents of Size Chart Link for the product "+result.getTitleofResult());
                  
                status = result.closeSizeChart(driver);

            }logStatus("TestCase 4", "End Test Case: Validated  of the Size Chart and the details Details", "Pass");;
    
    }
    @Test(description = "Verify Happy Flow of buying products", priority = 5, groups = {"Sanity"})
    @Parameters({"TC5_ProductNameToSearchFor","TC5_ProductNameToSearchFor2","TC5_AddressDetails"})
    public void TestCase05(String product1, String product2, String address) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");
       
        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue("Test Case Failure. Happy Flow Test Failed", status);
       
        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;
             // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();
        Thread.sleep(2000);
        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue("Step Failure : User Perform Login Failed, End TestCase 5: Happy Flow Test Failed", status);
       
        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart(product1);
        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart(product2);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" : "FAIL");
        
    }

    @Test(description = "Test Case 6: Verify that cart can be edited", priority = 6, groups = {"Regression"})
    @Parameters({"TC6_ProductNameToSearch1","TC6_ProductNameToSearch2",})
    public void TestCase06(String product1, String product2) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        SoftAssert as = new SoftAssert();
        as.assertTrue(status,"Step Failure : registration Failed");
        Assert.assertTrue("End Test case 6 : Verify that cart can be edited Failed ", status);
       
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        as.assertTrue(status, "User Perform Login Failed");
        Assert.assertTrue("End Test case 6 : Verify that cart can be edited Failed ", status);
       

        homePage.navigateToHome();
        status = homePage.searchForProduct("Xtend");
        homePage.addProductToCart(product1);

        status = homePage.searchForProduct("Yarine");
        homePage.addProductToCart(product2);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart("Yarine Floor Lamp", 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
            
        }

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();
        homePage.PerformLogout();

        logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        
    }
    @Test(description = "Verify that insufficient balance error is thrown when the wallet balance is not enough", priority = 7, groups = {"Sanity"})
    @Parameters({"TC7_ProductName","TC7_Qty"})
    public void TestCase07(String product1, int quantity1) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase",
                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
                "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        SoftAssert as = new SoftAssert();
        as.assertTrue(status,"Step Failure : registration Failed");
        Assert.assertTrue("End Test case :  Verify that insufficient balance error is thrown when the wallet balance is not enough Failed", status);
       
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        as.assertTrue(status,"Step Failure : login Failed");
        Assert.assertTrue("End Test case :  Verify that insufficient balance error is thrown when the wallet balance is not enough Failed", status);
     
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        homePage.addProductToCart(product1);
        homePage.changeProductQuantityinCart(product1, quantity1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();

        logStatus("End TestCase",
                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
                status ? "PASS" : "FAIL");

      }
      @Test(description = " Verify that product added to cart is available when a new tab is opened", priority = 8, groups = {"Regression"})
      public void  TestCase08() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase",
                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        SoftAssert as = new SoftAssert();
        as.assertTrue(status,"Step Failure : registration Failed");
        Assert.assertTrue("Test Case 8: Verify that product added to cart is available when a new tab is opened Failed ", status);
       
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        as.assertTrue(status,"Step Failure : Login Failed");
        Assert.assertTrue("Test Case 8: Verify that product added to cart is available when a new tab is opened Failed ", status);
     

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase",
        "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        status ? "PASS" : "FAIL");
      
    }
    @Test(description = "Verify that the Privacy Policy, About Us are displayed correctly", priority = 9, groups = {"Regression"})
    public void  TestCase09() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase",
                "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        SoftAssert as = new SoftAssert();
        as.assertTrue(status,"Step Failure : registration Failed");
        Assert.assertTrue("Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly Failed ", status);
       
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        as.assertTrue(status,"Step Failure : Login Failed");
        Assert.assertTrue("Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly Failed ", status);
       

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        as.assertTrue(status,"Step Failure :Verifying parent page url didn't change on privacy policy link click failed");
        Assert.assertTrue("Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly Failed ", status);

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        as.assertTrue(status,"Step Failure :Verifying new tab opened has Privacy Policy page heading failed");
        Assert.assertTrue("Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly Failed ", status);

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        as.assertTrue(status,"Step Failure :Verifying new tab opened has Terms Of Service page heading failed");
        Assert.assertTrue("Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly Failed ", status);

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase",
        "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        "PASS");
       
    }
    @Test(description = "Verify that contact us option is working correctly", priority = 10)
    public void TestCase10() throws InterruptedException {
        logStatus("Start TestCase",
                "Test Case 10: Verify that contact us option is working correctly ",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase10");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys("crio user");
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys("criouser@gmail.com");
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys("Testing the contact us page");

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        logStatus("End TestCase",
                "Test Case 10: Verify that contact us option is working correctly ",
                "PASS");

       
    }
    @Test(description = "Ensure that the Advertisement Links on the QKART page are clickable", priority = 11, groups = {"Sanity","Regression"})
        public void TestCase11() throws InterruptedException {
        Boolean status = false;
        logStatus("Start TestCase",
                "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase11");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        SoftAssert as = new SoftAssert();
        as.assertTrue(status,"Step Failure : registration Failed");
        Assert.assertTrue("Test Case 11:  Ensure that the links on the QKART advertisement are clickable Failed", status);
       
        takeScreenshot(driver, "Failure", "TestCase11");
       
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        as.assertTrue(status,"Step Failure : Login Failed");
        Assert.assertTrue("Test Case 11:  Ensure that the links on the QKART advertisement are clickable Failed", status);
      

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        logStatus("Step ", "Verify that 3 Advertisements are available", status ? "PASS" : "FAIL");

        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ? "PASS" : "FAIL");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ? "PASS" : "FAIL");

        logStatus("End TestCase",
                "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
                status ? "PASS" : "FAIL");
       
    }
}

