package com.bookstore.management.inventory.service;

import com.bookstore.management.inventory.dto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    Page<InventorySummaryDTO> findAll(Pageable pageable);
    InventoryResponseDTO findByBookId(Long bookId);
    Page<InventorySummaryDTO> findByActiveStatus(Boolean activeStatus, Pageable pageable);
    Page<InventorySummaryDTO> findLowStock(Pageable pageable);
    CheckAvailabilityResponseDTO checkBookAvailability (Long bookId);
    InventorySummaryDTO registerSale(UpdateStockDTO updateStockDTO, Long bookId);
    InventorySummaryDTO registerEntry(UpdateStockDTO updateStockDTO, Long bookId);
    InventorySummaryDTO positiveAdjustment(UpdateStockDTO updateStockDTO, Long bookId);
    InventorySummaryDTO negativeAdjustment(UpdateStockDTO updateStockDTO, Long bookId);
    InventorySummaryDTO create(CreateInventoryDTO createInventoryDTO);
    void releaseReservation(Long bookId, Integer quantity);
    void reserveStock(Long bookId, Integer quantity);
    InventoryResponseDTO updateThresholds(Long BookId,Integer stockMin, Integer stockMax);
    void disableById(Long inventoryId);
}
