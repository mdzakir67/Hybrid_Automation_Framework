package org.ge.vernova.api.event;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.ge.vernova.api.APIRequestSpec;
import org.ge.vernova.api.event.model.request.EventRequest;
import org.ge.vernova.api.event.model.request.EventQuery;

import static io.restassured.RestAssured.given;

public class EventsApiClient {
    public static String eventsURI="/events";

    public Response getEventsList(String token, EventQuery query) {

        RequestSpecification request = given()
                .spec(APIRequestSpec.authenticatedSpec(token));

        applyEventQuery(request,query);

        return request
                .when()
                .get(eventsURI).andReturn();
    }

    public Response getEventsList(EventQuery query) {

        RequestSpecification request = given()
                .spec(APIRequestSpec.defaultSpec());

        applyEventQuery(request,query);
        return request
                .when()
                .get(eventsURI).andReturn();
    }

    private void applyEventQuery(RequestSpecification request, EventQuery query) {

        if (query.getCity() != null) {
            request.queryParam("city", query.getCity());
        }

        if (query.getCategory() != null) {
            request.queryParam("category", query.getCategory());
        }

        if (query.getSearch() != null) {
            request.queryParam("search", query.getSearch());
        }

        if (query.getPage() != null) {
            request.queryParam("page", query.getPage());
        }

        if (query.getLimit() != null) {
            request.queryParam("limit", query.getLimit());
        }
    }

    public Response createEvent(String token, EventRequest eventRequest) {
        return given()
                .spec(APIRequestSpec.authenticatedSpec(token))
                .body(eventRequest)
                .when()
                .post(eventsURI).thenReturn();
    }

    public Response fetchEvent(String token, String eventID){
        return given().spec(APIRequestSpec.authenticatedSpec(token)).when().get(eventsURI + "/{id}", eventID).thenReturn();
    }

    public Response updateEvent(String token, String eventID, EventRequest request){
        return given().spec(APIRequestSpec.authenticatedSpec(token)).body(request).when().put(eventsURI + "/{id}", eventID).thenReturn();
    }

    public Response deleteEvent(String token, String eventID){
        return given().spec(APIRequestSpec.authenticatedSpec(token)).when().delete(eventsURI + "/{id}", eventID).thenReturn();
    }
}
