package com.bookstore.management.inventory.dto;

import com.bookstore.management.book.dto.BookSummaryDto;

public record InventorySummaryDTO(
        Long id,
        BookSummaryDto book,
        Integer quantityAvailable,
        Boolean activeState
){}
