package com.bookstore.management.shared.exception.custom;

import org.springframework.http.HttpStatus;

public class SaleAlreadyCompletedException extends BusinessException {
    public SaleAlreadyCompletedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
