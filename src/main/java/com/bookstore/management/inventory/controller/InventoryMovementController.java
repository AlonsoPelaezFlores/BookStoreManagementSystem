package com.bookstore.management.inventory.controller;

import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.service.InventoryMovementServiceImpl;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api/v1/movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final static int DEFAULT_SIZE = 10;
    private final static String DEFAULT_SORT = "createdAT";
    private final InventoryMovementServiceImpl inventoryMovementServiceImpl;

    @GetMapping(value = "/by-inventory/{inventoryId}")
    public ResponseEntity<Page<InventoryMovementResponseDTO>> getInventoryMovements(
            @Positive @PathVariable Long inventoryId,
            @PageableDefault(size = DEFAULT_SIZE,sort = DEFAULT_SORT, direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(inventoryMovementServiceImpl.findAllByInventoryId(inventoryId, pageable));
    }
    @GetMapping(value = "/by-type")
    public ResponseEntity<Page<InventoryMovementResponseDTO>> getMovementsByType(
            @RequestParam MovementType type,
            @PageableDefault(size = DEFAULT_SIZE,sort = DEFAULT_SORT, direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(inventoryMovementServiceImpl.findByMovementType(type,pageable));
    }
    @GetMapping(value = "/by-date-range")
    public ResponseEntity<Page<InventoryMovementResponseDTO>> getMovementsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = DEFAULT_SIZE,sort =DEFAULT_SORT, direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(inventoryMovementServiceImpl.findByDateRange(startDate,endDate,pageable));
    }


    @GetMapping(value = "/recent")
    public ResponseEntity<Page<InventoryMovementResponseDTO>> getRecentsMovements(
            @PageableDefault(size = DEFAULT_SIZE,sort =DEFAULT_SORT, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(inventoryMovementServiceImpl.findRecentMovements(pageable));
    }
}
