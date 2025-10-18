package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class InvalidAdjustmentException extends BusinessException {
    public InvalidAdjustmentException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
