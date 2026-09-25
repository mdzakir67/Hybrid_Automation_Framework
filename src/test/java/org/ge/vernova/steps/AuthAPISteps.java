package org.ge.vernova.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.ge.vernova.api.auth.AuthApiClient;
import org.ge.vernova.api.auth.model.request.AuthCredentials;
import org.ge.vernova.api.auth.model.response.registration.UserRegistrationFailure;
import org.ge.vernova.api.auth.model.response.AuthResponse;
import org.ge.vernova.context.ScenarioContext;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class AuthAPISteps {

    private final ScenarioContext context;
    private final AuthApiClient authApiClient;

    public AuthAPISteps(ScenarioContext context,AuthApiClient authApiClient){
        this.context=context;
        this.authApiClient = authApiClient;
    }

    @When("the user registers with username and password via api")
    public void registeruserStep(DataTable dataTable){
        List<Map<String,String>> data = dataTable.asMaps(String.class,String.class);
        String userName = context.fillSafely(data.get(0).get("userName"));
        String password = context.fillSafely(data.get(0).get("password"));
        AuthCredentials registerUser= new AuthCredentials(userName,password);
        Response response =
                authApiClient.registerUser(registerUser);
        context.set("registrationResponse", response);
        context.set("registeredUserName",userName);
        context.set("registeredPassword",password);
    }

    @Then("the registration should be successful")
    public void verifyRegistrationSuccessful() {
        Response response = context.get("registrationResponse",Response.class);
        AuthResponse result = response.as(AuthResponse.class);
        Assert.assertEquals(response.statusCode(),201);
        Assert.assertTrue(result.success(), "Registration was not successful");
        Assert.assertNotNull(result.token(), "Registration token was not returned");
        Assert.assertNotNull(result.user(), "User information was not returned");
        context.set("registrationResult", result);
        context.set("authToken",result.token());
    }

    @Then("the registration should fail with validation error")
    public void verifyRegistrationFailure(){
        Response response = context.get("registrationResponse",Response.class);
        UserRegistrationFailure result = response.as(UserRegistrationFailure.class);
        Assert.assertFalse(result.success(), "Registration was successful");
        Assert.assertEquals(result.error(), "Validation failed");
        Assert.assertEquals(result.details().get(0).field(), "email");
        Assert.assertEquals(result.details().get(0).message(), "A valid email is required");
        context.set("registrationFailureResult", result);
    }

    @Then("the user logs in with {string} username and {string} password")
    public void login(String username, String password){
        AuthCredentials credentials = new AuthCredentials(context.fillSafely(username),context.fillSafely(password));
        Response loginResponse = authApiClient.loginUser(credentials);
        this.context.set("loginResponse",loginResponse);
    }

    @Then("the user logged in successfully")
    public void verifyLogin(){
        Response response = context.get("loginResponse", Response.class);
        Assert.assertEquals(response.statusCode(), 200);
        AuthResponse result = response.as(AuthResponse.class);
        Assert.assertTrue(result.success(), "Login failed");
        Assert.assertNotNull(result.token(), "Token is empty");
        context.set("loginResult", result);
        context.set("authToken", result.token());
    }
}
