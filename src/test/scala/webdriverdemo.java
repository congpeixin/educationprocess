import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class webdriverdemo
{
    public static void main(String[] args) throws IOException
    {
        WebDriver driver = new FirefoxDriver();

        //Puts a Implicit wait, Will wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        //Launch website
        driver.navigate().to("http://www.baidu.com/");

        //Maximize the browser
        driver.manage().window().maximize();

        // Click on Math Calculators
        driver.findElement(By.xpath(".//*[@id='menu']/div[3]/a")).click();

        // Click on Percent Calculators
        driver.findElement(By.xpath(".//*[@id='menu']/div[4]/div[3]/a")).click();

        // Enter value 10 in the first number of the percent Calculator
        driver.findElement(By.id("cpar1")).sendKeys("10");

        // Enter value 50 in the second number of the percent Calculator
        driver.findElement(By.id("cpar2")).sendKeys("50");

        // Click Calculate Button
        driver.findElement(By.xpath(".//*[@id='content']/table/tbody/tr/td[2]/input")).click();

        // Get the Result Text based on its xpath
        String result = driver.findElement(By.xpath(".//*[@id='content']/p[2]/span/font/b")).getText();

        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

        FileUtils.copyFile(screenshot, new File("D:screenshotsscreenshots1.jpg"));

        //Print a Log In message to the screen
        System.out.println(" The Result is " + result);

        //Close the Browser.
        driver.close();
    }
}