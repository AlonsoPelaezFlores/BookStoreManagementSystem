package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class InsufficientReservedStockException extends BusinessException {
    public InsufficientReservedStockException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
