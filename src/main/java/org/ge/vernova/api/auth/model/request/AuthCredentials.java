package org.ge.vernova.api.auth.model.request;

public record AuthCredentials(
        String email,
        String password
) {
}
