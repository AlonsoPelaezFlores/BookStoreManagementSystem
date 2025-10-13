package com.bookstore.management.inventory.model;

import lombok.Getter;

@Getter
public enum AvailabilityStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    FEW_UNITS("Few Units");

    private final String description;
    AvailabilityStatus(String description) {
        this.description = description;
    }
}
