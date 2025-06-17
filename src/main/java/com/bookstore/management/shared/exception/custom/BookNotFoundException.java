package com.bookstore.management.shared.exception.custom;

public class BookNotFoundException extends ResourceNotFoundException {

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String resource, String field, Object value) {
        super(resource, field, value);
    }
}
