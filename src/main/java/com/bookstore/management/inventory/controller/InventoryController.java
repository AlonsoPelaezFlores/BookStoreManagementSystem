package com.bookstore.management.inventory.controller;

import com.bookstore.management.inventory.dto.*;
import com.bookstore.management.inventory.service.InventoryServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryServiceImpl inventoryServiceImpl;

    @GetMapping
    public ResponseEntity<List<InventorySummaryDTO>> getAll() {
        return ResponseEntity.ok(inventoryServiceImpl.findAll());
    }

    @GetMapping(value = "/book/{bookId}")
    public ResponseEntity<InventoryResponseDTO> getByBookId(
            @Positive @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryServiceImpl.findByBookId(bookId));
    }

    @GetMapping(value = "/status")
    public ResponseEntity<List<InventorySummaryDTO>> getAllByActiveStatus(
            @RequestParam(name = "active", defaultValue = "true") Boolean activeStatus) {
        return ResponseEntity.ok(inventoryServiceImpl.findByActiveStatusList(activeStatus));
    }

    @GetMapping(value = "/low-stock")
    public ResponseEntity<List<InventorySummaryDTO>> getByAlertLowStock() {
        return ResponseEntity.ok(inventoryServiceImpl.findByAlertLowStockList());
    }

    @GetMapping(value = "/book/{bookId}/available")
    public ResponseEntity<CheckAvailabilityResponseDTO> getBookAvailability(
            @Positive @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryServiceImpl.checkBookAvailability(bookId));
    }

    @PostMapping(value = "/book/{bookId}/sales")
    public ResponseEntity<InventorySummaryDTO> registerSale(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {

        return ResponseEntity.ok(inventoryServiceImpl.registerSale(stockDTO, bookId));
    }

    @PostMapping(value = "/book/{bookId}/entries")
    public ResponseEntity<InventorySummaryDTO> registerEntry(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {

        return ResponseEntity.ok(inventoryServiceImpl.registerEntry(stockDTO, bookId));
    }

    @PostMapping(value = "/book/{bookId}/adjustment/positive")
    public ResponseEntity<InventorySummaryDTO> positiveAdjustment(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {

        return ResponseEntity.ok(inventoryServiceImpl.positiveAdjustment(stockDTO, bookId));
    }

    @PostMapping(value = "/book/{bookId}/adjustment/negative")
    public ResponseEntity<InventorySummaryDTO> negativeAdjustment(
            @RequestBody @Valid UpdateStockDTO stockDTO,
            @Positive @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryServiceImpl.negativeAdjustment(stockDTO, bookId));
    }

    @PostMapping()
    public ResponseEntity<InventorySummaryDTO> create(
            @RequestBody @Valid CreateInventoryDTO inventoryDTO){
        return ResponseEntity.ok(inventoryServiceImpl.create(inventoryDTO));
    }
    @DeleteMapping(value = "/book/{bookId}/reservations")
    public ResponseEntity<String> releaseReservation(
            @Positive @PathVariable Long bookId,
            @Positive @RequestParam Integer quantity){
        inventoryServiceImpl.releaseReservation(bookId, quantity);
        return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/book/{bookId}/reservations")
    public ResponseEntity<String> reserveStock(
            @Positive @PathVariable Long bookId,
            @Positive @RequestParam Integer quantity){
        inventoryServiceImpl.reserveStock(bookId, quantity);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(value = "/book/{bookId}/thresholds")
    public ResponseEntity<InventoryResponseDTO> updateThresholds(
            @Positive @PathVariable Long bookId,
            @Positive @RequestParam Integer stockMin,
            @Positive @RequestParam Integer stockMax){
        return ResponseEntity.ok(inventoryServiceImpl.updateThresholds(bookId, stockMin, stockMax));
    }
    @PatchMapping(value = "/{inventoryId}/disable")
    public ResponseEntity<String> disableById(
            @Positive @PathVariable Long inventoryId){
        inventoryServiceImpl.disableById(inventoryId);
        return ResponseEntity.noContent().build();
    }
}