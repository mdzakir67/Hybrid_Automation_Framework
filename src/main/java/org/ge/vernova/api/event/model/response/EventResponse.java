package org.ge.vernova.api.event.model.response;

/**
 * {
 *     "success": true,
 *     "data": {
 *         "id": 3448,
 *         "title": "Tech Summit 2026",
 *         "description": "A premier technology conference.",
 *         "category": "Conference",
 *         "venue": "Bangalore International Centre",
 *         "city": "Bangalore",
 *         "eventDate": "2030-06-15T09:00:00.000Z",
 *         "price": "1500",
 *         "totalSeats": 500,
 *         "availableSeats": 500,
 *         "imageUrl": "https://example.com/banner.jpg",
 *         "isStatic": false,
 *         "userId": 2051,
 *         "createdAt": "2026-09-19T06:32:07.914Z",
 *         "updatedAt": "2026-09-19T06:32:07.914Z"
 *     },
 *     "message": "Event created successfully"
 * }
 */
public record EventResponse(
        boolean success,
        EventDataResponse data,
        String message
) {
}
