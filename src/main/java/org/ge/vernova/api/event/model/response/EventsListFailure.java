package org.ge.vernova.api.event.model.response;

public record EventsListFailure(
        boolean success,
        String error
) {
}
