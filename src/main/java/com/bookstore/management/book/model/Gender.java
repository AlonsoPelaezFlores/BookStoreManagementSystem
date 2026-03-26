package com.bookstore.management.book.model;

import lombok.Getter;

@Getter
public enum Gender{
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
    PREFER_NOT_TO_SAY("Prefer not to say");
    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

}
