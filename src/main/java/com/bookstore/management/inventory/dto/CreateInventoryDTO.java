package com.bookstore.management.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInventoryDTO(
        @NotNull(message = "the book id is mandatory")
        Long bookId,
        @NotNull(message = "Available quantity is mandatory")
        @Min(value = 0, message = "the available quantity cannot be negative")
        Integer quantityAvailable,
        @NotNull(message = "Minimum stock is mandatory")
        @Min(value = 0, message = "the minimum stock cannot be negative")
        Integer stockMin,
        @NotNull(message = "Maximum stock is mandatory")
        @Min(value = 1, message = "the maximum stock must be at least 1")
        Integer stockMax
){}
