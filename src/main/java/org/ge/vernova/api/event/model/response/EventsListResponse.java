package org.ge.vernova.api.event.model.response;

import java.util.List;

public record EventsListResponse(boolean success, List<EventDataResponse> data, EventPaginationResponse pagination) {
}
