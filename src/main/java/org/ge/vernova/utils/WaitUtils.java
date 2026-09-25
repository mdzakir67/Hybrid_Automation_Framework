package org.ge.vernova.utils;

import org.ge.vernova.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private WaitUtils() {
    }

    public static void waitForVisible(By locator) {
        new WebDriverWait(DriverManager.getDriver(), DEFAULT_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void waitForClickable(By locator) {
        new WebDriverWait(DriverManager.getDriver(), DEFAULT_TIMEOUT).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitForPresence(By locator) {
        new WebDriverWait(DriverManager.getDriver(), DEFAULT_TIMEOUT).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static void waitForTitle(String title) {
        new WebDriverWait(DriverManager.getDriver(), DEFAULT_TIMEOUT).until(ExpectedConditions.titleIs(title));
    }
    
    public static void waitForListToNotEmpty(By locator){
        new WebDriverWait(DriverManager.getDriver(), DEFAULT_TIMEOUT).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }
}
