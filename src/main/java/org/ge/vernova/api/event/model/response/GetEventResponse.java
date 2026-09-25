package org.ge.vernova.api.event.model.response;

/**
 * {
 *   "success": true,
 *   "data": {
 *     "id": 1,
 *     "title": "Tech Summit 2026",
 *     "description": "A premier technology conference bringing together industry leaders.",
 *     "category": "Conference",
 *     "venue": "Bangalore International Centre",
 *     "city": "Bangalore",
 *     "eventDate": "2026-06-15T09:00:00.000Z",
 *     "price": 1500,
 *     "totalSeats": 500,
 *     "availableSeats": 342,
 *     "imageUrl": "https://example.com/images/tech-summit.jpg",
 *     "createdAt": "2026-09-19T07:51:37.203Z",
 *     "updatedAt": "2026-09-19T07:51:37.203Z"
 *   }
 * }
 * @param success
 * @param data
 */
public record GetEventResponse(boolean success, EventDataResponse data) {
}
