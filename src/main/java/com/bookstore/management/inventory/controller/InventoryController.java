package com.bookstore.management.inventory.controller;

import com.bookstore.management.inventory.dto.*;
import com.bookstore.management.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final static int DEFAULT_SIZE = 10;
    private final static String DEFAULT_SORT = "id";

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventorySummaryDTO>> getAll(
            @PageableDefault(size = DEFAULT_SIZE, sort = DEFAULT_SORT) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.findAll(pageable));
    }

    @GetMapping(value = "/book/{bookId}")
    public ResponseEntity<InventoryResponseDTO> getByBookId(
            @Positive @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryService.findByBookId(bookId));
    }

    @GetMapping(value = "/status")
    public ResponseEntity<List<InventorySummaryDTO>> getAllByActiveStatus(
            @RequestParam(name = "active", defaultValue = "true") Boolean activeStatus) {
        return ResponseEntity.ok(inventoryService.findByActiveStatusList(activeStatus));
    }

    @GetMapping(value = "/low-stock")
    public ResponseEntity<List<InventorySummaryDTO>> getByAlertLowStock() {
        return ResponseEntity.ok(inventoryService.findByAlertLowStockList());
    }

    @GetMapping(value = "/book/{bookId}/available")
    public ResponseEntity<CheckAvailabilityResponseDTO> getBookAvailability(
            @Positive @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryService.checkBookAvailability(bookId));
    }

    @PostMapping(value = "/book/{bookId}/sales")
    public ResponseEntity<InventorySummaryDTO> registerSale(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {

        return ResponseEntity.ok(inventoryService.registerSale(stockDTO, bookId));
    }

    @PostMapping(value = "/book/{bookId}/entries")
    public ResponseEntity<InventorySummaryDTO> registerEntry(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {

        return ResponseEntity.ok(inventoryService.registerEntry(stockDTO, bookId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/book/{bookId}/adjustment/positive")
    public ResponseEntity<InventorySummaryDTO> positiveAdjustment(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {

        return ResponseEntity.ok(inventoryService.positiveAdjustment(stockDTO, bookId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/book/{bookId}/adjustment/negative")
    public ResponseEntity<InventorySummaryDTO> negativeAdjustment(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryService.negativeAdjustment(stockDTO, bookId));
    }

    @PostMapping()
    public ResponseEntity<InventorySummaryDTO> create(
            @RequestBody @Valid CreateInventoryDTO inventoryDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(inventoryDTO));
    }
    @DeleteMapping(value = "/book/{bookId}/reservations")
    public ResponseEntity<Void> releaseReservation(
            @Positive @PathVariable Long bookId,
            @Positive @RequestParam Integer quantity){
        inventoryService.releaseReservation(bookId, quantity);
        return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/book/{bookId}/reservations")
    public ResponseEntity<Void> reserveStock(
            @Positive @PathVariable Long bookId,
            @Positive @RequestParam Integer quantity){
        inventoryService.reserveStock(bookId, quantity);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/book/{bookId}/thresholds")
    public ResponseEntity<InventoryResponseDTO> updateThresholds(
            @Positive @PathVariable Long bookId,
            @Positive @RequestParam Integer stockMin,
            @Positive @RequestParam Integer stockMax){
        return ResponseEntity.ok(inventoryService.updateThresholds(bookId, stockMin, stockMax));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{inventoryId}/disable")
    public ResponseEntity<Void> disableById(
            @Positive @PathVariable Long inventoryId){
        inventoryService.disableById(inventoryId);
        return ResponseEntity.noContent().build();
    }
}