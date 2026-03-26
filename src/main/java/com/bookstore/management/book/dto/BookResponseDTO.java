package com.bookstore.management.book.dto;

import java.math.BigDecimal;

public record BookResponseDTO(
        Long id,
        String isbn,
        String title,
        String description,
        Integer pages,
        BigDecimal price,
        AuthorSummaryDTO author
) {
}
