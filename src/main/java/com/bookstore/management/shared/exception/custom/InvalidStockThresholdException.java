package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class InvalidStockThresholdException extends BusinessException {
    public InvalidStockThresholdException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
