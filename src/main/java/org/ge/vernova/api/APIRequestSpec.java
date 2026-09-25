package org.ge.vernova.api;

import io.restassured.authentication.AuthenticationScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.ge.vernova.api.filter.ApiLoggingFilter;
import org.ge.vernova.config.ApiConfig;

public final class APIRequestSpec {
    private APIRequestSpec(){}

    private static RequestSpecBuilder getBaseSpec(){
        return new RequestSpecBuilder().setBaseUri(ApiConfig.getBaseUrl()).setContentType(ContentType.JSON).addFilter(new ApiLoggingFilter());
    }

    public static RequestSpecification defaultSpec(){
        return getBaseSpec().build();
    }

    public static RequestSpecification authenticatedSpec(String token){
        return getBaseSpec().addHeader("Authorization","Bearer "+token).build();
    }
}
