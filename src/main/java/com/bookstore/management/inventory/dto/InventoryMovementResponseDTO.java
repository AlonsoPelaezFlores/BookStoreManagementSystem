package com.bookstore.management.inventory.dto;

import com.bookstore.management.inventory.model.MovementType;
import java.time.LocalDateTime;

public record InventoryMovementResponseDTO(
    Long id,
    InventorySummaryDTO inventorySummaryDTO,
    Integer affectedQuantity,
    Integer quantityBefore,
    Integer quantityAfter,
    MovementType movementType,
    String description,
    String createBy,
    LocalDateTime createdAt
){}
