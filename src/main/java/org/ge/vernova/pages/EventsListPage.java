package org.ge.vernova.pages;

import org.ge.vernova.driver.DriverManager;
import org.ge.vernova.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

public class EventsListPage {

    private final By eventCards = By.id("event-card");
    private final By eventIds = By.cssSelector(".p-4 a:first-child");

    public List<WebElement> getListOfEvents(){
        WaitUtils.waitForListToNotEmpty(eventCards);
        return DriverManager.getDriver().findElements(eventCards);
    }

    public List<String> getListOfEventIds(){
        return getListOfEvents().stream().map(webElement -> Objects.requireNonNull(webElement.findElement(eventIds).getAttribute("href")).split("[//]")[4]).toList();
    }

}
