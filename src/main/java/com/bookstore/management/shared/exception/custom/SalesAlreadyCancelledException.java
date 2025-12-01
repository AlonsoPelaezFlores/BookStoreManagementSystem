package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class SalesAlreadyCancelledException extends BusinessException {
    public SalesAlreadyCancelledException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
