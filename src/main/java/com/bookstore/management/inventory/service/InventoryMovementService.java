package com.bookstore.management.inventory.service;

import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.model.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface InventoryMovementService {
    Page<InventoryMovementResponseDTO> findAllByInventoryId(Long inventoryId, Pageable pageable);
    Page<InventoryMovementResponseDTO> findByMovementType(MovementType movementType, Pageable pageable);
    Page<InventoryMovementResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<InventoryMovementResponseDTO> findRecentMovements(Pageable pageable);
}
