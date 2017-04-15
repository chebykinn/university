package lab;

import org.junit.Before;
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
public class BlogTest {
    Util util = new Util();

    private void doAddEntry(WebDriver driver){
        util.prepare(driver);
        util.auth(driver, util.getCorrectLogin(), util.getCorrectPassword());

        util.tryClick(driver, By.xpath("//a[text()='Личный блог']"));
        util.tryClick(driver, By.xpath("//a[text()='Написать в блог']"));
        driver.findElement(By.id("jtitle")).clear();
        driver.findElement(By.id("jtitle")).sendKeys("test");
        driver.findElement(By.id("text")).clear();
        driver.findElement(By.id("text")).sendKeys("test");
        util.tryClick(driver, By.xpath("(//button[@type='submit'])[2]"));

        driver.quit();

    }

    private void doRemoveEntry(WebDriver driver) throws InterruptedException {
        util.prepare(driver);
        util.auth(driver, util.getCorrectLogin(), util.getCorrectPassword());

        util.tryClick(driver, By.xpath("//a[text()='Личный блог']"));
        util.tryClick(driver, By.xpath("//a[text()='test']"));
        util.tryClick(driver, By.xpath("//button[text()='Удалить']"));
        Alert javascriptAlert = driver.switchTo().alert();
        assertEquals("Вы действительно хотите удалить эту запись?", javascriptAlert.getText());
        javascriptAlert.accept();

        driver.quit();

    }

    private void doBookmarkEntry(WebDriver driver) throws InterruptedException {
        util.prepare(driver);
        util.auth(driver, util.getCorrectLogin(), util.getCorrectPassword());

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class, 'js-top-nav-sub')]")));

        util.tryClick(driver, By.xpath("//a[@class='c-link'][1]"));
        util.tryClick(driver, By.cssSelector("button.r-button-unstyled.c-bookmarks-button"));
        assertEquals(true, util.isElementPresent(driver, By.xpath("//button[contains(@class, 'c-bookmarks-button') and contains(@class, 'is-active')]")));

        driver.quit();

    }

    @Test
    public void addEntry(){
        doAddEntry(new FirefoxDriver());
        doAddEntry(new ChromeDriver());

    }

    @Test
    public void removeEntry() throws InterruptedException {
        doRemoveEntry(new FirefoxDriver());
        doRemoveEntry(new ChromeDriver());

    }

    @Test
    public void bookmarkEntry() throws InterruptedException {
        doBookmarkEntry(new FirefoxDriver());
        doBookmarkEntry(new ChromeDriver());

    }
}
