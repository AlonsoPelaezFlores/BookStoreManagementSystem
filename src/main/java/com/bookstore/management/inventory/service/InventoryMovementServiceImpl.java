package com.bookstore.management.inventory.service;

import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.mapper.InventoryMovementMapper;
import com.bookstore.management.inventory.model.InventoryMovement;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.repository.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    private final InventoryMovementMapper inventoryMovementMapper;
    private final InventoryMovementRepository inventoryMovementRepository;
    @Override
    public Page<InventoryMovementResponseDTO> findAllByInventoryId(Long inventoryId, Pageable pageable) {

        Page<InventoryMovement> page =  inventoryMovementRepository.findAllByInventoryId(inventoryId, pageable);
        return page.map(inventoryMovementMapper::toInventoryMovementResponseDTO);
    }

    @Override
    public Page<InventoryMovementResponseDTO> findByMovementType(MovementType movementType, Pageable pageable) {

        Page<InventoryMovement> page =  inventoryMovementRepository.findByMovementType(movementType, pageable);
        return page.map(inventoryMovementMapper::toInventoryMovementResponseDTO);
    }

    @Override
    public Page<InventoryMovementResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        Page<InventoryMovement> page = inventoryMovementRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return page.map(inventoryMovementMapper::toInventoryMovementResponseDTO);
    }

    @Override
    public Page<InventoryMovementResponseDTO> findRecentMovements(Pageable pageable) {
        Page<InventoryMovement> page = inventoryMovementRepository.findAllBy(pageable);
        return page.map(inventoryMovementMapper::toInventoryMovementResponseDTO);
    }
}


