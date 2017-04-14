package lab;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import static org.junit.Assert.assertEquals;

/**
 * Created by ivan on 14.04.17.
 */
public class ExperienceTest {
    private Util util;

    private void doSelectCar(WebDriver driver) {
        util.prepare(driver);
        driver.findElement(By.name("brand")).click();
        new Select(driver.findElement(By.name("brand"))).selectByVisibleText("Acura");
        driver.findElement(By.id("model")).click();
        new Select(driver.findElement(By.id("model"))).selectByVisibleText("RSX");
        driver.findElement(By.cssSelector("option[value=\"3\"]")).click();
        driver.findElement(By.id("experience")).click();
        driver.findElement(By.id("t")).click();
        new Select(driver.findElement(By.id("t"))).selectByVisibleText("обо всём");
        driver.findElement(By.xpath("(//option[@value=''])[2]")).click();
        driver.findElement(By.name("mode")).click();
        assertEquals(true, util.isElementPresent(driver, By.cssSelector("div.c-car-card")));
        driver.quit();

    }


    @Before
    public void setUp() throws Exception {
        util = new Util();
    }

    @Test
    public void selectCar() throws Exception {
        doSelectCar(new FirefoxDriver());
        doSelectCar(new ChromeDriver());
    }

}
