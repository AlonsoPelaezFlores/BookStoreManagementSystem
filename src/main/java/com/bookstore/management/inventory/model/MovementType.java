package com.bookstore.management.inventory.model;

import lombok.Getter;

@Getter
public enum MovementType {
    ENTRY("Entry"),
    EXIT("Exit"),
    POSITIVE_ADJUSTMENT("Positive Adjustment"),
    NEGATIVE_ADJUSTMENT("Negative Adjustment"),
    RETURN("Return"),
    RESERVE("Reserve"),
    RELEASE_RESERVE("Release Reserve"),
    INITIAL_INVENTORY("Initial Inventory"),
    UPDATE_THRESHOLD("Update Threshold"),
    DISABLE("Disable");

    private final String description;

    MovementType(String description) {
        this.description = description;
    }
}
