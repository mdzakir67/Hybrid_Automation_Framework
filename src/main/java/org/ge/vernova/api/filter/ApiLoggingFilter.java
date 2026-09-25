package org.ge.vernova.api.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiLoggingFilter implements Filter {

    private static final Logger log =
            LoggerFactory.getLogger(ApiLoggingFilter.class);

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext context) {

        log.info("API Request: {} {}",
                requestSpec.getMethod(),
                requestSpec.getURI());

        log.info("Request Headers: {}", requestSpec.getHeaders());

        if (requestSpec.getBody() != null) {
            log.info("Request Body: {}", ApiLogSanitizer.sanitize(requestSpec.getBody().toString()));
        }

        Response response = context.next(requestSpec, responseSpec);

        log.info("API Response: {} {}",
                response.statusCode(),
                requestSpec.getURI());

        log.info("Response Headers: {}", response.getHeaders());

        if (response.getBody() != null) {
            log.info("Response Body: {}", ApiLogSanitizer.sanitize(response.getBody().asPrettyString()));
        }

        return response;
    }
}