package org.ge.vernova.api.auth.model.response.registration;


import java.util.List;

public record UserRegistrationFailure(
        boolean success,
        String error,
        List<UserRegistrationErrorDetails> details) { }
