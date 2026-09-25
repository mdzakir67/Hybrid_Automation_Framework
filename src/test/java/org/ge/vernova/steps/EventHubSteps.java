package org.ge.vernova.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.ge.vernova.context.ScenarioContext;
import org.ge.vernova.pages.EventsHubLandingPage;
import org.ge.vernova.pages.EventsListPage;
import org.testng.Assert;

public class EventHubSteps {

    private final ScenarioContext context;
    private final EventsHubLandingPage eventsHubLandingPage;
    private final EventsListPage eventsListPage;

    public EventHubSteps(ScenarioContext context, EventsHubLandingPage eventsHubLandingPage,EventsListPage page) {
        this.context = context;
        this.eventsHubLandingPage = eventsHubLandingPage;
        this.eventsListPage = page;
    }

    @Given("I open the EventHub application")
    public void openEventHubApplication() {
        this.eventsHubLandingPage.open();
    }

    @Then("the EventHub application should be displayed")
    public void verifyEventHubApplicationDisplayed() {
        Assert.assertEquals(eventsHubLandingPage.getPageTitle(),"EventHub — Discover & Book Events");
        System.out.println(this.context.get("title",String.class));
    }

    @Then("the user logs in with the registered user")
    public void uiLogin(){
        this.eventsHubLandingPage.login(this.context.get("registeredUserName",String.class),this.context.get("registeredPassword",String.class));
    }

    @Then("^the user logs in with the username (\\S+)$")
    public void uiLoginWithUser(String userName){
        this.eventsHubLandingPage.login(context.fillSafely(userName), this.context.get("registeredPassword",String.class));
    }

    @Then("^the user navigates to the (Home|Events|My Bookings) page$")
    public void navigateToPage(String page){
        this.eventsHubLandingPage.navigateToPage(page);
    }

    @Then("^the user verifies, if the event with id (\\S+) exists$")
    public void verifyEventExists(String eventId){
        Assert.assertTrue(eventsListPage.getListOfEventIds().contains(context.fillSafely(eventId)), "Event with ID " + eventId + " does not exist");
    }
}
