package org.ge.vernova.pages;

import org.ge.vernova.config.UIConfig;
import org.ge.vernova.utils.ElementActions;
import org.openqa.selenium.By;

public class EventsHubLandingPage extends BasePage {
    private final By userNameField = By.id("email");
    private final By passWordField = By.id("password");
    private final By loginButton = By.id("login-btn");


    public void login(String userName,String password){
        ElementActions.type(userNameField,userName);
        ElementActions.type(passWordField,password);
        ElementActions.click(loginButton);
    }

    public void open(){
        getDriver().get(UIConfig.getBaseUrl()+"/login");
    }

    public void navigateToPage(String linkText){
        ElementActions.navigateToPage(By.linkText(linkText));
    }
}
