package org.ge.vernova.api.event.model.response;

import java.util.Objects;

/**
 * Event item response model returned by the Events API.
 */
public record EventDataResponse(
        Integer id,
        String title,
        String description,
        String category,
        String venue,
        String city,
        String eventDate,
        String price,
        Integer totalSeats,
        Integer availableSeats,
        String imageUrl,
        boolean isStatic,
        Integer userId,
        String createdAt,
        String updatedAt
) {

    public boolean sameAs(EventDataResponse other) {
        if (other == null) {
            return false;
        }

        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.category, other.category)
                && Objects.equals(this.venue, other.venue)
                && Objects.equals(this.city, other.city)
                && Objects.equals(this.eventDate, other.eventDate)
                && Objects.equals(this.price, other.price)
                && Objects.equals(this.totalSeats, other.totalSeats)
                && Objects.equals(this.availableSeats, other.availableSeats)
                && Objects.equals(this.imageUrl, other.imageUrl);
    }
}
