import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by ivan on 12.04.17.
 */
public class SeleniumTest {
    @Test
    public void executeFirefoxDriver() throws MalformedURLException {
        this.execute(DesiredCapabilities.firefox());
    }

    @Test
    public void executeChrome() throws MalformedURLException {
        this.execute(DesiredCapabilities.chrome());
    }

    private void execute(final DesiredCapabilities capability) throws MalformedURLException {
        WebDriver driver = new RemoteWebDriver(
//                new URL("http://localhost:4444/wd/hub"), capability
                new URL("http://52.59.146.127:4444/wd/hub"), capability

        );
        driver.get("http://www.drive2.ru");
        String title = driver.getTitle();
        assertEquals("DRIVE2.RU", title);
        driver.quit();
    }
}
