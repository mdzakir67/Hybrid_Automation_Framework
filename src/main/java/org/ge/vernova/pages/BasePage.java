package org.ge.vernova.pages;

import org.ge.vernova.driver.DriverManager;
import org.openqa.selenium.WebDriver;

public abstract class BasePage {

    protected WebDriver getDriver(){
        return DriverManager.getDriver();
    }

    public String getPageTitle(){
        return getDriver().getTitle();
    }
}
