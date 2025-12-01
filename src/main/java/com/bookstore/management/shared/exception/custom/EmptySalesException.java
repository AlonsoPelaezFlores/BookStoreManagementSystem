package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class EmptySalesException extends BusinessException {
    public EmptySalesException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
