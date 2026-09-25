Feature: EventHub application

  Scenario: Register User Via API
    Given the user registers with username and password via api
      | userName                | password  |
      | test{scenarioId}@example.com | Admin@123 |
    When the registration should be successful
    Then I open the EventHub application
    And the EventHub application should be displayed
    When the user logs in with the registered user
    And the user logs in with "test{scenarioId}@example.com" username and "Admin@123" password
    And the user logged in successfully

  Scenario: Register User Via API and Fetch Events
    Given the user registers with username and password via api
      | userName              | password  |
      | test{scenarioId}@example.com | Admin@123 |
    When the registration should be successful
    And the user fetches the list of events by city "Delhi"
    Then the user verifies the eventsList response returned status code as 200
    And the events list response should be successful
    Then the user verifies the events in list belong to "Delhi" city

  Scenario: Register User Via API and Fetch Events Without Auth
    And the user fetches the list of events by city "Delhi" without authentication
    Then the user verifies the eventsList response returned status code as 401
    And the events list response failed with error message "Unauthorized"


  Scenario: Register user with invalid email
    When the user registers with username and password via api
      | userName | password  |
      | invalid  | Admin@123 |
    Then the registration should fail with validation error


  Scenario: CRUD on Events API
    Given the user logs in with "testd131b906@example.com" username and "Admin@123" password
    And the user logged in successfully
    When the user creates an event with following details and stored id in {eventID}:
      | title       | Tech Summit 2038                 |
      | description | A premier technology conference. |
      | category    | Conference                       |
      | venue       | Bangalore International Centre   |
      | city        | Bangalore                        |
      | eventDate   | 2030-06-15T09:00:00.000Z         |
      | price       | 1500                             |
      | totalSeats  | 500                              |
      | imageUrl    | https://example.com/banner.jpg   |
    Then the api call is successful with status code 201
    And the user verifies the created event details:
      | title       | Tech Summit 2038                 |
      | description | A premier technology conference. |
      | category    | Conference                       |
      | venue       | Bangalore International Centre   |
      | city        | Bangalore                        |
      | eventDate   | 2030-06-15T09:00:00.000Z         |
      | price       | 1500                             |
      | totalSeats  | 500                              |
      | imageUrl    | https://example.com/banner.jpg   |
  And the user verifies the event with id {eventID} matches following:
      | title       | Tech Summit 2038                 |
      | description | A premier technology conference. |
      | category    | Conference                       |
      | venue       | Bangalore International Centre   |
      | city        | Bangalore                        |
      | eventDate   | 2030-06-15T09:00:00.000Z         |
      | price       | 1500                             |
      | totalSeats  | 500                              |
      | imageUrl    | https://example.com/banner.jpg   |
  And the user fetches the list of events with following filters:
    | city     | Bangalore                        |
    | search   | A premier technology conference. |
    | category | Conference                       |
    | page     | 1                                |
    | limit    | 1                                |
  Then the events list response should be successful
  And the user verifies the events in list belong to "Bangalore" city
  And the user updates the event with id {eventID} for following fields:
    | title       | Tech Summit 2040                    |
    | description | A premier qa technology conference. |
    | category    | Conference for QA                   |
    | venue       | Bangalore International Centre   |
    | city        | Bangalore                        |
    | eventDate   | 2030-06-15T09:00:00.000Z         |
    | price       | 1500                             |
    | totalSeats  | 500                              |
    | imageUrl    | https://example.com/banner.jpg   |
  #And the api call is successful with status code 200
#  And the user verifies the event with id {eventID} matches following:
#    | title       | Tech Summit 2040                    |
#    | description | A premier qa technology conference. |
#    | category    | Conference for QA                   |
#    | venue       | Bangalore International Centre      |
#    | city        | Bangalore                           |
#    | eventDate   | 2030-06-15T09:00:00.000Z            |
#    | price       | 1500                                |
#    | totalSeats  | 500                                 |
#    | imageUrl    | https://example.com/banner.jpg      |
  Then the user deletes the event with id {eventID}
  And the api call is successful with status code 200
