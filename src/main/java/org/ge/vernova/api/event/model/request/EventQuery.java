package org.ge.vernova.api.event.model.request;

public class EventQuery {

    private final String category;
    private final String city;
    private final String search;
    private final Integer page;
    private final Integer limit;

    private EventQuery(Builder builder) {
        this.category = builder.category;
        this.city = builder.city;
        this.search = builder.search;
        this.page = builder.page;
        this.limit = builder.limit;
    }


    public static Builder builder() {
        return new Builder();
    }

    public String getCategory() {
        return category;
    }

    public String getCity() {
        return city;
    }

    public String getSearch() {
        return search;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getLimit() {
        return limit;
    }

    public static class Builder {

        private String category;
        private String city;
        private String search;
        private Integer page;
        private Integer limit;

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder search(String search) {
            this.search = search;
            return this;
        }

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public EventQuery build() {
            return new EventQuery(this);
        }
    }
}