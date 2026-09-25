package org.ge.vernova.api.auth.model.response.registration;

public record UserRegistrationErrorDetails(
    String field,
    String message
) { }
