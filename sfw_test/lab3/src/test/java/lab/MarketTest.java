package lab;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertEquals;

/**
 * Created by ivan on 14.04.17.
 */
public class MarketTest {
    private Util util;

    private void doFindOffer(WebDriver driver) {
        util.prepare(driver);
        driver.findElement(By.xpath("//div[@id='l-content']/div[1]/div[2]/div[3]/div/div/ul/li[1]/a")).click();
        util.tryClick(driver, By.cssSelector("a.market-categories__subcategory.c-link"));
        driver.findElement(By.xpath("//div[@id='l-content']/div[2]/div[2]/div[2]/div/div/div[2]/div/a")).click();
        driver.quit();
    }


    @Before
    public void setUp() throws Exception {
        util = new Util();
    }

    @Test
    public void findOffer() throws Exception {
        doFindOffer(new FirefoxDriver());
        doFindOffer(new ChromeDriver());
    }

}
