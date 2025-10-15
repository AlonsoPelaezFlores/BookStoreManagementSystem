package com.bookstore.management.book.dto;

public record BookSummaryDTO(
    Long id,
    String title,
    String isbn,
    String author
){}
