package id.winnicode.horizon.data.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NewsCategory {
    GENERAL,
    HEALTH,
    SCIENCE,
    BUSINESS,
    TECHNOLOGY,
    SPORTS,
    ENTERTAINMENT;

    @JsonValue
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
