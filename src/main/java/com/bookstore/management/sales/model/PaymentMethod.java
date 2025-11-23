package com.bookstore.management.sales.model;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("Cash"),
    CARD("Card"),
    TRANSFER("Transfer");
    private final String description;
    PaymentMethod(String description) {
        this.description = description;
    }
}
