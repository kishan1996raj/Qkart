package QKART_SANITY_LOGIN.Module1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Footer {
    RemoteWebDriver driver;

    public Footer(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public boolean clickPrivacyPolicy() throws InterruptedException {
        try {
            WebElement privacyTag = driver.findElement(By.xpath("//a[text()='Privacy policy']"));
            privacyTag.click();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyPrivacyPolicy() {
        try {
            WebElement privacyPolicy =
                    driver.findElement(By.xpath("//h2[text()='Privacy Policy']"));
            boolean status = privacyPolicy.isDisplayed();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clickTermsOfService() {
        try {
            WebElement tOSTag = driver.findElement((By.xpath("//a[text()='Terms of Service']")));
            tOSTag.click();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifytOSPage(){
        try{
            WebElement element = driver.findElement(By.xpath(""));
            boolean status = element.isDisplayed();
            return status;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifytAboutuslink(){
        try{
            WebElement element = driver.findElement(By.xpath("//a[text()='About us']"));
            boolean status = element.isDisplayed();
            if(status){
                System.out.println("About us link verified");
            }
            return status;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean contactUs(String name, String email, String message){
        boolean status = false;
        try{
            WebElement contactUsButton = driver.findElement(By.xpath("//p[text()='Contact us']"));
            contactUsButton.click();
            WebElement nameTextArea = driver.findElement(By.xpath("//input[@placeholder='Name']"));
            nameTextArea.sendKeys(name);
           WebElement emailTextarea = driver.findElement(By.xpath("//input[@placeholder='Email']"));
            emailTextarea.sendKeys(email);
            WebElement messageTextarea = driver.findElement(By.xpath("//input[@placeholder='Message']"));
            messageTextarea.sendKeys(message);
            WebElement contactNowButton = driver.findElement(By.xpath("//button[text()=' Contact Now']"));
            contactNowButton.click();
            status = true;
            return status;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
