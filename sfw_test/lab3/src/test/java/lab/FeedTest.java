package lab;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;

/**
 * Created by ivan on 14.04.17.
 */
public class FeedTest {
    Util util = new Util();

    private void doViewFeed(WebDriver driver){
        util.prepare(driver);
        util.auth(driver, util.getCorrectLogin(), util.getCorrectPassword());
        driver.get(util.getBaseUrl()+"/my/feed");
        driver.quit();

    }

    @Test
    public void viewFeed() throws InterruptedException {
        doViewFeed(new FirefoxDriver());
        doViewFeed(new ChromeDriver());

    }
}
