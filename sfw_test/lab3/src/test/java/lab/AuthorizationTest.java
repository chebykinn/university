package lab;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by ivan on 14.04.17.
 */
public class AuthorizationTest {
    Util util;

    private void doSuccessfulLogin(WebDriver driver){
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        util.prepare(driver);
        util.auth(driver, util.getCorrectLogin(), util.getCorrectPassword());

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class, 'js-top-nav-sub')]")));
        util.tryClick(driver, By.xpath("//button[contains(@class, 'js-top-nav-sub')]"));
        driver.findElement(By.linkText("Выход")).click();
        driver.quit();

    }

    private void doWrongLogin(WebDriver driver){
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        util.prepare(driver);
        util.auth(driver, util.getCorrectLogin(), "asdasdasd");
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("span.field-validation-error")));
        driver.quit();

    }

    @Before
    public void setUp() throws Exception {
        util = new Util();
    }


    @Test
    public void successfulLogin() throws Exception {
        doSuccessfulLogin(new FirefoxDriver());
        doSuccessfulLogin(new ChromeDriver());
    }

    @Test
    public void wrongPassword() throws Exception {
        doWrongLogin(new FirefoxDriver());
        doWrongLogin(new ChromeDriver());
    }
}
