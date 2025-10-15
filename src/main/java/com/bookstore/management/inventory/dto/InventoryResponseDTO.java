package com.bookstore.management.inventory.dto;

import com.bookstore.management.book.dto.BookSummaryDTO;


import java.time.LocalDateTime;

public record InventoryResponseDTO(
    Long id,
    BookSummaryDTO book,
    Integer quantityAvailable,
    Integer quantityReserved,
    Integer stockMin,
    Integer stockMax,
    LocalDateTime lastUpdate,
    Boolean activeState,
    Integer realStockAvailable
){}
