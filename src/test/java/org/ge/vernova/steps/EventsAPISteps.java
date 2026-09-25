package org.ge.vernova.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.ge.vernova.api.event.EventsApiClient;
import org.ge.vernova.api.event.model.request.EventQuery;
import org.ge.vernova.api.event.model.request.EventRequest;
import org.ge.vernova.api.event.model.response.*;
import org.ge.vernova.context.ScenarioContext;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class EventsAPISteps {

    private final ScenarioContext context;
    private final EventsApiClient eventsApiClient;


    public EventsAPISteps(ScenarioContext context, EventsApiClient eventsApiClient) {
        this.context = context;
        this.eventsApiClient = eventsApiClient;
    }

    @When("the user fetches the list of events by city {string}")
    public void fetchEventsList(String city) {
        EventQuery query = EventQuery.builder().city(city).build();
        Response response = this.eventsApiClient.getEventsList( this.context.get("authToken",String.class),query);
        context.setLastApiResponse(response);
    }

    @When("the user fetches the list of events by city {string} without authentication")
    public void fetchEventsListWithOutAuth(String city) {
        EventQuery query = EventQuery.builder().city(city).build();
        Response response = this.eventsApiClient.getEventsList(query);
        context.setLastApiResponse(response);
    }

    @When("^the user fetches the list of events with following filters:$")
    public void fetchEventsListWithOutAuth(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        EventQuery query = EventQuery.builder().city(data.get("city"))
                .limit(Integer.parseInt(data.get("limit")))
                .page(Integer.parseInt(data.get("page")))
                .search(data.get("search"))
                .category(data.get("category")).build();
        Response response = this.eventsApiClient.getEventsList(context.get("authToken", String.class), query);
        context.setLastApiResponse(response);
    }

    @When("the user verifies the eventsList response returned status code as {int}")
    public void verifyFetchEventStatusCode(Integer statusCode){
        Response response = context.getLastApiResponse();
        Assert.assertEquals(response.statusCode(),statusCode);
    }

    @Then("the events list response should be successful")
    public void verifyEventsListResponse() {

        Response response = context.getLastApiResponse();

        EventsListResponse result =
                response.as(EventsListResponse.class);

        Assert.assertTrue(
                result.success(),
                "Events API request was not successful"
        );

        context.set("eventsList", result.data());
    }

    @Then("the events list response failed with error message {string}")
    public void verifyEventsListFailure(String message){
        Response response = context.getLastApiResponse();
        EventsListFailure result = response.as(EventsListFailure.class);
        Assert.assertEquals(result.error(),message,"Event API error message did not match");
    }

    @Then("the user verifies the events in list belong to {string} city")
    public void verifyCityMappingToEvent(String city) {
        Response response = context.getLastApiResponse();
        EventsListResponse result = response.as(EventsListResponse.class);
        List<EventDataResponse> eventsList = (List<EventDataResponse>) result.data();
        for (EventDataResponse event : eventsList) {
            Assert.assertEquals(event.city(), city, "The event city did not match for event with id %d".formatted(event.id()));
        }
    }

    /**
     * Example Value
     * Schema
     * {
     *   "title": "Tech Summit 2026",
     *   "description": "A premier technology conference.",
     *   "category": "Conference",
     *   "venue": "Bangalore International Centre",
     *   "city": "Bangalore",
     *   "eventDate": "2026-06-15T09:00:00.000Z",
     *   "price": 1500,
     *   "totalSeats": 500,
     *   "imageUrl": "https://example.com/banner.jpg"
     * }
     * @param dataTable
     */

    @And("^the user creates an event with following details and stored id in \\{(\\S+)\\}:$")
    public void createEventWithDetails(String eventID,DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        EventRequest eventRequest = buildEventRequest(data);
        Response response = eventsApiClient.createEvent(context.get("authToken", String.class), eventRequest);
        context.setLastApiResponse(response);
        context.set(eventID, response.as(EventResponse.class).data().id());
    }


    @Then("the api call is successful with status code {int}")
    public void verifyApiStatusCode(int statusCode){
        Response response = context.getLastApiResponse();
        Assert.assertEquals(response.getStatusCode(), statusCode, "Expected API call to return HTTP " + statusCode);
    }

    @Then("the user verifies the created event details:")
    public void verifyCreatedEventDetails(DataTable dataTable) {
        Response response = context.getLastApiResponse();
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        EventResponse result = response.as(EventResponse.class);
        Assert.assertTrue(result.success(),"Event creation failed");
        Assert.assertEquals(result.message(), "Event created successfully");
        EventDataResponse expectedData = new EventDataResponse(0, data.get("title"), data.get("description"), data.get("category"), data.get("venue"), data.get("city"), data.get("eventDate"), data.get("price"), Integer.valueOf(data.get("totalSeats")), Integer.valueOf(data.get("totalSeats")), data.get("imageUrl"), false, null, null, null);
        Assert.assertTrue(result.data().sameAs(expectedData), "Created event details do not match");
    }

    @Then("^the user verifies the event with id (\\S+) matches following:$")
    public void verifyEventWithID(String eventID,DataTable dataTable){
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String filledEventID = context.fillSafely(eventID);
        Response fetchEventResponse = eventsApiClient.fetchEvent(context.get("authToken", String.class), filledEventID);
        Assert.assertEquals(fetchEventResponse.getStatusCode(), 200);
        GetEventResponse getEventResponse = fetchEventResponse.as(GetEventResponse.class);
        EventDataResponse expectedData = new EventDataResponse(0, data.get("title"), data.get("description"), data.get("category"), data.get("venue"), data.get("city"), data.get("eventDate"), data.get("price"), Integer.valueOf(data.get("totalSeats")), Integer.valueOf(data.get("totalSeats")), data.get("imageUrl"), false, null, null, null);
        Assert.assertTrue(getEventResponse.data().sameAs(expectedData));
    }

    @Then("^the user updates the event with id (\\S+) for following fields:$")
    public void updateEvent(String eventID, DataTable dataTable){
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String filledEventID = context.fillSafely(eventID);
        EventRequest request = buildEventRequest(data);
        context.setLastApiResponse(eventsApiClient.updateEvent(context.get("authToken", String.class), filledEventID, request));
    }

    @Then("^the user deletes the event with id (\\S+)$")
    public void deleteEvent(String eventID){
        context.setLastApiResponse(eventsApiClient.deleteEvent(context.get("authToken",String.class),context.fillSafely(eventID)));
    }


    //###############################HELPER  METHODS #############################//

    private EventRequest buildEventRequest(Map<String, String> data) {
        EventRequest.Builder builder = EventRequest.builder();

        if (data != null) {
            if (containsValue(data, "title")) builder.title(data.get("title"));
            if (containsValue(data, "description")) builder.description(data.get("description"));
            if (containsValue(data, "category")) builder.category(data.get("category"));
            if (containsValue(data, "venue")) builder.venue(data.get("venue"));
            if (containsValue(data, "city")) builder.city(data.get("city"));
            if (containsValue(data, "eventDate")) builder.eventDate(data.get("eventDate"));
            if (containsValue(data, "date")) builder.eventDate(data.get("date"));
            if (containsValue(data, "price")) builder.price(Integer.valueOf(data.get("price")));
            if (containsValue(data, "totalSeats")) builder.totalSeats(Integer.valueOf(data.get("totalSeats")));
            if (containsValue(data, "imageUrl")) builder.imageUrl(data.get("imageUrl"));
        }

        return builder.build();
    }

    private boolean containsValue(Map<String, String> data, String key) {
        return data.containsKey(key) && data.get(key) != null && !data.get(key).isBlank();
    }

}
