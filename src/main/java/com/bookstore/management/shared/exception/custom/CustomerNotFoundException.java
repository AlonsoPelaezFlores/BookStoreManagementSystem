package com.bookstore.management.shared.exception.custom;

public class CustomerNotFoundException extends ResourceNotFoundException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
    public CustomerNotFoundException(String resource,String field, Object value) {
        super(String.format(" %s not found with %s: '%s'", resource, field, value));
    }
}
