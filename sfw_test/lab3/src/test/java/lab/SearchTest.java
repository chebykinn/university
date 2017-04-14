package lab;

import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by ivan on 14.04.17.
 */
public class SearchTest {
    private Util util;

    private void doSearch(WebDriver driver, String query){
        driver.findElement(By.name("q")).clear();
        driver.findElement(By.name("q")).sendKeys(query);
        driver.findElement(By.xpath("//div[@id='l-content']/div/div[3]/div/form/div[2]/button")).click();

    }

    private void doBadSearch(WebDriver driver){
        util.prepare(driver);
        doSearch(driver, "fdgyrtfghdrfhjdrtfhdrtyhxrdtyh");
        boolean hasNoResultsMessage = util.isElementPresent(driver, By.cssSelector("div.gs-no-results-result"));
        assertEquals(true, hasNoResultsMessage);
        driver.quit();
    }

    private void doGoodSearch(WebDriver driver){
        util.prepare(driver);
        doSearch(driver, "tesla");
        boolean hasResultsBlock = util.isElementPresent(driver, By.cssSelector("table.gsc-table-result"));
        assertEquals(true, hasResultsBlock);
        driver.quit();
    }

    @Before
    public void setUp() throws Exception {
        util = new Util();
    }

    @Test
    public void failedSearch() throws Exception {
        doBadSearch(new FirefoxDriver());
        doBadSearch(new ChromeDriver());
    }

    @Test
    public void successfulSearch() throws Exception {
        doGoodSearch(new FirefoxDriver());
        doGoodSearch(new ChromeDriver());
    }
}
