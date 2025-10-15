package com.bookstore.management.inventory.dto;

import com.bookstore.management.book.dto.BookSummaryDTO;

public record InventorySummaryDTO(
        Long id,
        BookSummaryDTO book,
        Integer quantityAvailable,
        Boolean activeState
){}
