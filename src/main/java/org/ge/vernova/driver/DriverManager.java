package org.ge.vernova.driver;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);
    private static final  ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager(){}

    public static void initializeDriver(){
        log.info("Initializing Driver");
        DRIVER.set(DriverFactory.createWebDriver());
    }

    public static WebDriver getDriver(){
        return DRIVER.get();
    }

    public static void tearDown(){
        log.info("Tearing down Driver");
        WebDriver driver = DRIVER.get();
        if(Objects.nonNull(driver)){
            driver.quit();
            DRIVER.remove();
        }
    }
}
