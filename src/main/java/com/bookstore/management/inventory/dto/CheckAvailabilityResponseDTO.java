package com.bookstore.management.inventory.dto;

import com.bookstore.management.inventory.model.AvailabilityStatus;
import lombok.Builder;

@Builder
public record CheckAvailabilityResponseDTO(
        Long bookId,
        Boolean isAvailable,
        AvailabilityStatus status,
        Integer quantityAvailable
) {}
