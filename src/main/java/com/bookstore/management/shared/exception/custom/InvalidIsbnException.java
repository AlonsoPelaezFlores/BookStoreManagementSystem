package com.bookstore.management.shared.exception.custom;

public class InvalidIsbnException extends BusinessException {
    public InvalidIsbnException(String message) {
        super(message);
    }
}
