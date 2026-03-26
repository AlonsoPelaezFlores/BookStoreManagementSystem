package com.bookstore.management.book.dto;

import java.math.BigDecimal;

public record BookSummaryDTO(
    Long id,
    String isbn,
    String title,
    BigDecimal price,
    String author
){}
