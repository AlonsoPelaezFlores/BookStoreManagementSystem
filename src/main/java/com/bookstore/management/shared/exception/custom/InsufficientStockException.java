package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
