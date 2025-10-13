package com.bookstore.management.inventory.dto;

import com.bookstore.management.inventory.model.AvailabilityStatus;

public record CheckAvailabilityResponseDTO(
        Long bookId,
        Boolean isAvailable,
        AvailabilityStatus status,
        Integer quantityAvailable
) {}
