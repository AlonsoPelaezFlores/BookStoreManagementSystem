package com.bookstore.management.inventory.service;

import com.bookstore.management.inventory.dto.*;

import java.util.List;

public interface InventoryService {

    List<InventorySummaryDTO> findAll();
    InventoryResponseDTO findByBookId(Long bookId);
    List<InventorySummaryDTO> findByActiveStatusList(Boolean activeStatus);
    List<InventorySummaryDTO> findByAlertLowStockList();
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
