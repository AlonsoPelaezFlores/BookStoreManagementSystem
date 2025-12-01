package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class SalesAlreadyCompletedException extends BusinessException {
    public SalesAlreadyCompletedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
