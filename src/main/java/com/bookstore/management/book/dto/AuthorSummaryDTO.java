package com.bookstore.management.book.dto;

import com.bookstore.management.book.model.Gender;

public record AuthorSummaryDTO(
        Long id,
        String name,
        String nationality,
        Gender gender
) {
}
