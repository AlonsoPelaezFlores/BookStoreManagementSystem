package com.bookstore.management.book.dto;

import java.math.BigDecimal;

public record BookSummaryDTO(
    Long id,
    String title,
    String isbn,
    BigDecimal price,
    String author
){}
