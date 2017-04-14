package lab;

import lab.Util;
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
public class CarCatalogTest {
    private Util util;

    private void findShortList(WebDriver driver) {
        util.prepare(driver);
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("h3.c-header")));
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("a.c-link")));
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("a.c-block__more")));
        driver.quit();
    }

    private void goToFullList(WebDriver driver) {
        util.prepare(driver);
        driver.findElement(By.cssSelector("a.c-block__more")).click();
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("a.c-link")));
        driver.quit();
    }

    private void doFindCar(WebDriver driver) {
        util.prepare(driver);
        driver.findElement(By.cssSelector("a.c-block__more")).click();
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("a.c-link")));
        driver.findElement(By.className("c-link")).click();
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("div.o-grid__item")));
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("a.c-car-title")));
        driver.findElement(By.cssSelector("a.c-car-title")).click();
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("div.c-car-info")));
        driver.quit();
    }


    @Before
    public void setUp() throws Exception {
        util = new Util();
    }

    @Test
    public void shortList() throws Exception {
        findShortList(new FirefoxDriver());
        findShortList(new ChromeDriver());
    }

    @Test
    public void fullList() throws Exception {
        goToFullList(new FirefoxDriver());
        goToFullList(new ChromeDriver());
    }

    @Test
    public void findCar() throws Exception {
        doFindCar(new FirefoxDriver());
        doFindCar(new ChromeDriver());
    }

}
