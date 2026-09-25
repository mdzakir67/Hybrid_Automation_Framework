package org.ge.vernova.api.event.model.request;

/**
 * Represents a request to create or update an event.
 *
 * {
 *   "title": "Tech Summit 2026",
 *   "description": "A premier technology conference.",
 *   "category": "Conference",
 *   "venue": "Bangalore International Centre",
 *   "city": "Bangalore",
 *   "eventDate": "2026-06-15T09:00:00.000Z",
 *   "price": 1500,
 *   "totalSeats": 500,
 *   "imageUrl": "https://example.com/banner.jpg"
 * }
 */
public record EventRequest(
        String title,
        String description,
        String category,
        String venue,
        String city,
        String eventDate,
        Integer price,
        Integer totalSeats,
        String imageUrl
) {
    private EventRequest(Builder builder) {
        this(
                builder.title,
                builder.description,
                builder.category,
                builder.venue,
                builder.city,
                builder.eventDate,
                builder.price,
                builder.totalSeats,
                builder.imageUrl
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private String category;
        private String venue;
        private String city;
        private String eventDate;
        private Integer price;
        private Integer totalSeats;
        private String imageUrl;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder venue(String venue) {
            this.venue = venue;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder eventDate(String eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public Builder price(Integer price) {
            this.price = price;
            return this;
        }

        public Builder totalSeats(Integer totalSeats) {
            this.totalSeats = totalSeats;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public EventRequest build() {
            return new EventRequest(this);
        }
    }
}
