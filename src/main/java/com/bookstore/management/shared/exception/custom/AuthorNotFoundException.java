package com.bookstore.management.shared.exception.custom;

public class AuthorNotFoundException extends ResourceNotFoundException{

    public AuthorNotFoundException(String message) {
        super(message);
    }

    public AuthorNotFoundException(String resource, String field, Object value) {
        super(resource, field, value);
    }
}
