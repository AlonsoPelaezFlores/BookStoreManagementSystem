package com.bookstore.management.inventory.dto;

import com.bookstore.management.inventory.model.MovementType;
import jakarta.validation.constraints.*;


public record UpdateStockDTO(
        @NotNull(message = "Quantity adjustment is required")
        Integer quantityAdjustment,

        @NotNull(message = "Movement type is required")
        MovementType movementType,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotBlank(message = "User is required")
        @Size(max = 100, message = "Username cannot exceed 100 characters")
        String updatedBy
) {}
