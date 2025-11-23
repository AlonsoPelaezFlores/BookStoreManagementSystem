package com.bookstore.management.sales.model;

import lombok.Getter;

@Getter
public enum SalesStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");
    private final String description;
    SalesStatus(String description) {
        this.description = description;
    }
}

