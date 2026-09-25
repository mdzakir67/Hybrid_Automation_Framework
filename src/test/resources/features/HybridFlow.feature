Feature: Hybrid Flow Feature

  Scenario: Register User, Create Event and Verify Event in UI
    Given the user registers with username and password via api
      | userName            | password  |
      | testxo{scenarioId}@example.com | Admin@123 |
    And the registration should be successful
    When the user logs in with "testxo{scenarioId}@example.com" username and "Admin@123" password
    And the user logged in successfully
    Then the user creates an event with following details and stored id in {eventID}:
      | title       | Tech Summit 2038                 |
      | description | A premier technology conference. |
      | category    | Conference                       |
      | venue       | Bangalore International Centre   |
      | city        | Bangalore                        |
      | eventDate   | 2030-06-15T09:00:00.000Z         |
      | price       | 1500                             |
      | totalSeats  | 500                              |
      | imageUrl    | https://example.com/banner.jpg   |
    And the api call is successful with status code 201
    Then I open the EventHub application
    And the EventHub application should be displayed
    And the user logs in with the username testxo{scenarioId}@example.com
    Then the user navigates to the Events page
    And the user verifies, if the event with id {eventID} exists

