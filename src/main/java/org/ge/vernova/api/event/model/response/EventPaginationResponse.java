package org.ge.vernova.api.event.model.response;

/**
 *  "pagination": {
 *     "total": 48,
 *     "page": 1,
 *     "limit": 10,
 *     "totalPages": 5
 *   }
 */

public record EventPaginationResponse(
        Integer total,
        Integer page,
        Integer limit,
        Integer totalPages
) {
}
