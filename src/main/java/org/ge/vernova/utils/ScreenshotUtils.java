package org.ge.vernova.utils;

import org.ge.vernova.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class ScreenshotUtils {

    public static byte[] captureScreenshot() {
        WebDriver driver = DriverManager.getDriver();
        if(Objects.isNull(driver)){
            return new byte[0];
        }
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
