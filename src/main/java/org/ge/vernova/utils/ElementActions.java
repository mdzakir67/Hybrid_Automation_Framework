package org.ge.vernova.utils;

import org.ge.vernova.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public final class ElementActions {

    private ElementActions() {
    }

    public static void click(By locator) {
        WaitUtils.waitForClickable(locator);
        DriverManager.getDriver().findElement(locator).click();
    }

    public static void type(By locator, String text) {
        WaitUtils.waitForVisible(locator);
        WebElement element = DriverManager.getDriver().findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public static void navigateToPage(By linkText){
        WaitUtils.waitForVisible(linkText);
        DriverManager.getDriver().findElement(linkText).click();
    }
}
