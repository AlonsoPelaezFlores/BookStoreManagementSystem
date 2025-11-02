package com.bookstore.management.inventory.service;

import com.bookstore.management.inventory.dto.*;
import com.bookstore.management.inventory.mapper.InventoryMapper;
import com.bookstore.management.inventory.model.AvailabilityStatus;
import com.bookstore.management.inventory.model.Inventory;
import com.bookstore.management.inventory.model.InventoryMovement;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.repository.InventoryMovementRepository;
import com.bookstore.management.inventory.repository.InventoryRepository;
import com.bookstore.management.shared.exception.custom.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final InventoryMovementRepository inventoryMovementRepository;

    @Override
    public List<InventorySummaryDTO> findAll() {
        List<Inventory> inventorySummaries = inventoryRepository.findAll();
        return inventoryMapper.toInventorySummaryDTOList(inventorySummaries);
    }

    @Override
    public InventoryResponseDTO findByBookId(Long bookId) {
        return inventoryMapper.toInventoryResponseDTO(findByBookIdOrThrow(bookId));
    }

    private Inventory findByBookIdOrThrow(Long bookId) {
        return inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book","Id",bookId));
    }

    @Override
    public List<InventorySummaryDTO> findByActiveStatusList(Boolean activeStatus) {

        List<Inventory> activeInventories = inventoryRepository.findByActiveStatus(activeStatus);
        return inventoryMapper.toInventorySummaryDTOList(activeInventories);
    }

    @Override
    public List<InventorySummaryDTO> findByAlertLowStockList() {
        List<Inventory> activeInventories = inventoryRepository.findActiveInventoriesWithLowStock();
        return inventoryMapper.toInventorySummaryDTOList(activeInventories);
    }

    @Override
    public CheckAvailabilityResponseDTO checkBookAvailability(Long bookId) {
        Inventory inventory = findByBookIdOrThrow(bookId);

        int quantity = inventory.getQuantityAvailable();
        AvailabilityStatus status = determinateAvailabilityStatus(quantity, inventory.getStockMin());

        return CheckAvailabilityResponseDTO.builder()
                .bookId(inventory.getBook().getId())
                .isAvailable(quantity > 0)
                .status(status)
                .quantityAvailable(inventory.getQuantityAvailable())
                .build();
    }
    private AvailabilityStatus determinateAvailabilityStatus(Integer quantity, Integer stockMin) {

        if (quantity > stockMin) {
            return AvailabilityStatus.AVAILABLE;
        }else if (quantity > 0) {
            return AvailabilityStatus.FEW_UNITS;
        }
        else {
            return AvailabilityStatus.UNAVAILABLE;
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public InventorySummaryDTO registerSale(UpdateStockDTO updateStockDTO, Long bookId) {

        Inventory inventory = findByBookIdOrThrow(bookId);

        if (inventory.getQuantityAvailable() < updateStockDTO.quantityAdjustment()){
            throw new InsufficientStockException("Insufficient Stock");
        }
        int quantityBefore = inventory.getQuantityAvailable();
        int quantityAfter = inventory.getQuantityAvailable() - updateStockDTO.quantityAdjustment();
        int affectedQuantity = quantityAfter - quantityBefore;

        inventory.setQuantityAvailable(quantityAfter);
        inventory.setAlertLowStock(quantityAfter <= inventory.getStockMin());

        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .quantityAfter(quantityAfter)
                .quantityBefore(quantityBefore)
                .affectedQuantity(affectedQuantity)
                .movementType(updateStockDTO.movementType())
                .description(updateStockDTO.movementType().getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);

        return inventoryMapper.toInventorySummaryDTO(inventory);
    }

    @Transactional
    @Override
    public InventorySummaryDTO registerEntry(UpdateStockDTO updateStockDTO, Long bookId) {

        Inventory inventory = findByBookIdOrThrow(bookId);

        int quantityBefore = inventory.getQuantityAvailable();
        int quantityAfter = inventory.getQuantityAvailable() + updateStockDTO.quantityAdjustment();
        int affectedQuantity = quantityAfter - quantityBefore;

        inventory.setQuantityAvailable(quantityAfter);
        inventory.setAlertLowStock(quantityAfter <= inventory.getStockMin());
        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .quantityAfter(quantityAfter)
                .quantityBefore(quantityBefore)
                .affectedQuantity(affectedQuantity)
                .movementType(updateStockDTO.movementType())
                .description(updateStockDTO.movementType().getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);

        return inventoryMapper.toInventorySummaryDTO(inventory);
    }

    @Transactional
    @Override
    public InventorySummaryDTO positiveAdjustment(UpdateStockDTO updateStockDTO, Long bookId) {

        Inventory inventory = findByBookIdOrThrow(bookId);

        int quantityBefore = inventory.getQuantityAvailable();
        int quantityAfter = inventory.getQuantityAvailable() + updateStockDTO.quantityAdjustment();
        int affectedQuantity = quantityAfter - quantityBefore;

        inventory.setQuantityAvailable(quantityAfter);
        inventory.setAlertLowStock(quantityAfter <= inventory.getStockMin());
        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .quantityAfter(quantityAfter)
                .quantityBefore(quantityBefore)
                .affectedQuantity(affectedQuantity)
                .movementType(updateStockDTO.movementType())
                .description(updateStockDTO.movementType().getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);

        return inventoryMapper.toInventorySummaryDTO(inventory);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public InventorySummaryDTO negativeAdjustment(UpdateStockDTO updateStockDTO, Long bookId) {

        Inventory inventory = findByBookIdOrThrow(bookId);

        int quantityAfter = inventory.getQuantityAvailable() - updateStockDTO.quantityAdjustment();

        if ( quantityAfter < 0){
            throw new InvalidAdjustmentException("The adjustment would leave negative stock.");
        }
        int quantityBefore = inventory.getQuantityAvailable();
        int affectedQuantity = quantityAfter - quantityBefore;

        inventory.setQuantityAvailable(quantityAfter);
        inventory.setAlertLowStock(quantityAfter <= inventory.getStockMin());
        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .quantityAfter(quantityAfter)
                .quantityBefore(quantityBefore)
                .affectedQuantity(affectedQuantity)
                .movementType(updateStockDTO.movementType())
                .description(updateStockDTO.movementType().getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);

        return inventoryMapper.toInventorySummaryDTO(inventory);
    }

    @Transactional
    @Override
    public InventorySummaryDTO create(CreateInventoryDTO createInventoryDTO) {
        if (inventoryRepository.findByBookId(createInventoryDTO.bookId()).isPresent()) {
            throw new DuplicateEntityException("Inventory","BookId",createInventoryDTO.bookId());
        }
        Inventory inventory = inventoryMapper.toEntity(createInventoryDTO);

        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .affectedQuantity(inventory.getQuantityAvailable())
                .quantityBefore(0)
                .quantityAfter(inventory.getQuantityAvailable())
                .movementType(MovementType.INITIAL_INVENTORY)
                .description(MovementType.INITIAL_INVENTORY.getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);

        return inventoryMapper.toInventorySummaryDTO(inventory);
    }

    @Transactional
    @Override
    public void releaseReservation(Long bookId, Integer quantity) {
        Inventory inventory = findByBookIdOrThrow(bookId);
        if (inventory.getQuantityReserved() < quantity) {
            throw new InsufficientReservedStockException("Not enough reserved stock to release");
        }

        int quantityBefore = inventory.getQuantityAvailable();
        int quantityAfter = inventory.getQuantityAvailable() + quantity;
        int affectedQuantity = quantityAfter - quantityBefore;

        inventory.setQuantityReserved(inventory.getQuantityReserved() - quantity);
        inventory.setQuantityAvailable(quantityAfter);
        inventory.setAlertLowStock(quantityAfter <= inventory.getStockMin());
        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .affectedQuantity(affectedQuantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .movementType(MovementType.RELEASE_RESERVE)
                .description(MovementType.RELEASE_RESERVE.getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);
    }

    @Transactional
    @Override
    public void reserveStock(Long bookId, Integer quantity) {
        Inventory inventory = findByBookIdOrThrow(bookId);
        if (inventory.getQuantityAvailable() < quantity) {
            throw new InsufficientStockException("Insufficient stock");
        }

        int quantityBefore = inventory.getQuantityAvailable();
        int quantityAfter = inventory.getQuantityAvailable() - quantity;
        int affectedQuantity = quantityAfter - quantityBefore;

        inventory.setQuantityAvailable(quantityBefore);
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        inventory.setAlertLowStock(quantityAfter <= inventory.getStockMin());
        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .affectedQuantity(affectedQuantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .movementType(MovementType.RESERVE)
                .description(MovementType.RESERVE.getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);
    }

    @Transactional
    @Override
    public InventoryResponseDTO updateThresholds(Long bookId, Integer stockMin, Integer stockMax) {

        Inventory inventory = findByBookIdOrThrow(bookId);

        if (stockMin >= stockMax){
            throw new InvalidStockThresholdException ("Minimum stock cannot be greater than or equal to the maximum stock");
        }
        inventory.setStockMin(stockMin);
        inventory.setStockMax(stockMax);
        inventory.setAlertLowStock(inventory.getQuantityAvailable() <= inventory.getStockMin());
        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .affectedQuantity(0)
                .quantityBefore(inventory.getQuantityAvailable())
                .quantityAfter(inventory.getQuantityAvailable())
                .movementType(MovementType.UPDATE_THRESHOLD)
                .description(MovementType.UPDATE_THRESHOLD.getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);

        return inventoryMapper.toInventoryResponseDTO(inventory);
    }

    @Transactional
    @Override
    public void disableById(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory","Id", inventoryId));

        inventory.setActiveStatus(false);

        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .affectedQuantity(0)
                .quantityBefore(inventory.getQuantityAvailable())
                .quantityAfter(inventory.getQuantityAvailable())
                .movementType(MovementType.DISABLE)
                .description(MovementType.DISABLE.getDescription())
                .build();

        inventoryMovementRepository.save(inventoryMovement);
    }
}
