package com.bookstore.management.shared.exception.custom;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
    public DuplicateEntityException(String entity, String field, Object value) {
        super(String.format("%s with %s '%s' already exists", entity, field, value));
    }
}
