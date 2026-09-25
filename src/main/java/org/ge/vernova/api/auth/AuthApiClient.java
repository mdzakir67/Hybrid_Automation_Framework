package org.ge.vernova.api.auth;

import io.restassured.response.Response;
import org.ge.vernova.api.APIRequestSpec;
import org.ge.vernova.api.auth.model.request.AuthCredentials;

import static io.restassured.RestAssured.given;

public class AuthApiClient {

    private static final String registerURI = "/auth/register";
    private static final String loginURI = "/auth/login";

    public Response registerUser(AuthCredentials registerUser){
        return given().spec(APIRequestSpec.defaultSpec()).body(registerUser).when().post(registerURI).andReturn();
    }

    public Response loginUser(AuthCredentials loginRequest){
        return given().spec(APIRequestSpec.defaultSpec()).body(loginRequest).when().post(loginURI).andReturn();
    }

}
