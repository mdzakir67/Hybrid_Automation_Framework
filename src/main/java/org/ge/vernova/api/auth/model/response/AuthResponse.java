package org.ge.vernova.api.auth.model.response;


import org.ge.vernova.api.auth.model.response.registration.UserResponse;

public record AuthResponse(
        boolean success,
        String token,
        UserResponse user
) {
}
