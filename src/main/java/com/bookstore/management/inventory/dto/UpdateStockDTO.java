package com.bookstore.management.inventory.dto;

import com.bookstore.management.inventory.model.MovementType;
import jakarta.validation.constraints.*;


public record UpdateStockDTO(
        @NotNull(message = "Quantity adjustment is required")
        @Positive
        Integer quantityAdjustment,

        @NotNull(message = "Movement type is required")
        MovementType movementType
) {}
