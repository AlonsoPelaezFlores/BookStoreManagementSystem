package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class EmptySaleException extends BusinessException {
    public EmptySaleException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
