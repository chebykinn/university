package lab;

import org.openqa.selenium.*;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/**
 * Created by ivan on 14.04.17.
 */
public class Util {
    public String getBaseUrl() {
        return baseUrl;
    }

    private String baseUrl;
    private String correctLogin = "testdrive2@10host.top";
    private String correctPassword = "1234567890lab";

    public String getCorrectLogin() {
        return correctLogin;
    }

    public String getCorrectPassword() {
        return correctPassword;
    }

    public Util() {
        System.setProperty("webdriver.gecko.driver", "/home/ivan/root/data/university/3/6/stuff/sfw_test/selenium/geckodriver");
        System.setProperty("webdriver.chrome.driver", "/home/ivan/root/data/university/3/6/stuff/sfw_test/selenium/chromedriver");
        baseUrl = "https://www.drive2.ru/";
    }

    public void prepare(WebDriver driver){
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(getBaseUrl());
    }

    public boolean isElementPresent(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void tryClick(WebDriver driver, By selector) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));

//        element.click();
        element.sendKeys(Keys.ENTER);
    }

    public void auth(WebDriver driver, String login, String password){
        String loginPath = "input[name=login].c-top-loginform__input";
        String passwordPath = "input[name=password].c-top-loginform__input";
        driver.findElement(By.xpath("//button[@onclick=\"yaCounter.reachGoal('login_from_top_by_email'); return true;\"]")).click();
        driver.findElement(By.cssSelector(loginPath)).clear();
        driver.findElement(By.cssSelector(loginPath)).sendKeys(login);
        driver.findElement(By.cssSelector(passwordPath));
        driver.findElement(By.cssSelector(passwordPath)).sendKeys(password);
        driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();
    }
}
