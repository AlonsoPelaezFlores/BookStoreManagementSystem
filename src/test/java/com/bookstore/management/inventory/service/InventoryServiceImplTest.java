package com.bookstore.management.inventory.service;

import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.inventory.dto.*;
import com.bookstore.management.inventory.mapper.InventoryMapper;
import com.bookstore.management.inventory.model.AvailabilityStatus;
import com.bookstore.management.inventory.model.Inventory;
import com.bookstore.management.inventory.model.InventoryMovement;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.repository.InventoryMovementRepository;
import com.bookstore.management.inventory.repository.InventoryRepository;
import com.bookstore.management.shared.exception.custom.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @InjectMocks
    private InventoryServiceImpl inventoryService;
    @Spy
    private InventoryMapper inventoryMapper = Mappers.getMapper(InventoryMapper.class);

    private Author author;
    private Book book;
    private Book book2;
    private Inventory inventory;
    private Inventory anotherInventory;
    private CreateInventoryDTO createInventoryDTO;
    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(1L)
                .name("Chimamanda Ngozi Adichie")
                .nationality("Nigerian")
                .gender(Author.Gender.FEMALE)
                .build();

        book = Book.builder()
                .id(1L)
                .isbn("9780007356348")
                .title("Half of a Yellow Sun")
                .publishDate(LocalDate.of(2006, 8, 10))
                .description("A story set during the Nigerian Civil War.")
                .pages(433)
                .genre("Historical Fiction")
                .author(author)
                .build();
        book2 =Book.builder()
                .id(2L)
                .isbn("9780307455925")
                .title("Americanah")
                .publishDate(LocalDate.of(2013, 5, 14))
                .description("A powerful story of love and race spanning Nigeria and America.")
                .pages(588)
                .genre("Contemporary")
                .author(author)
                .build();

        createInventoryDTO = new CreateInventoryDTO(
                book.getId(),
                100,
                10,
                500
        );

        inventory = Inventory.builder()
                .id(1L)
                .book(book)
                .quantityAvailable(100)
                .stockMin(10)
                .stockMax(500)
                .build();

        anotherInventory = Inventory.builder()
                .id(2L)
                .book(book2)
                .quantityAvailable(200)
                .stockMin(20)
                .stockMax(1000)
                .build();
    }
    @Nested
    @DisplayName("Find All")
    class findAll{

        @Test
        @DisplayName("Should return list of inventory summaries when inventories exist")
        void shouldReturnListOfInventorySummariesWhenInventoriesExist(){

            List<Inventory> inventories = Arrays.asList(inventory, anotherInventory);

            when(inventoryRepository.findAll()).thenReturn(inventories);

            List<InventorySummaryDTO> result = inventoryService.findAll();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).id());
            assertEquals(100, result.get(0).quantityAvailable());
            assertEquals(1L, result.get(0).book().id());
            assertEquals(2L, result.get(1).id());
            assertEquals(200, result.get(1).quantityAvailable());
            assertEquals(2L, result.get(1).book().id());
            verify(inventoryRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no inventories exist")
        void shouldReturnEmptyListWhenNoInventoriesExist() {

            List<Inventory> emptyInventories = List.of();

            when(inventoryRepository.findAll()).thenReturn(emptyInventories);

            List<InventorySummaryDTO> result = inventoryService.findAll();

            assertThat(result).hasSize(0);
            assertThat(result).hasSize(0).isEqualTo(List.of());
            assertThat(result).isEmpty();
            verify(inventoryRepository, times(1)).findAll();
        }
    }
    @Nested
    @DisplayName("Find by book id")
    class findByBookId{

        @Test
        @DisplayName("Should return inventory response when book exists in inventory")
        void shouldReturnInventoryResponseWhenBookExistsInInventory(){

            Long bookId = 1L;
            when(inventoryRepository.findById(bookId)).thenReturn(Optional.of(inventory));

            InventoryResponseDTO result = inventoryService.findByBookId(bookId);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.quantityAvailable()).isEqualTo(100);
            assertThat(result.stockMin()).isEqualTo(10);
            assertThat(result.stockMax()).isEqualTo(500);
            assertThat(result.activeStatus()).isTrue();
            verify(inventoryRepository, times(1)).findById(bookId);

        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory() {

            Long bookId = 999L;

            when(inventoryRepository.findById(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.findByBookId(bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findById(bookId);
        }
    }
    @Nested
    @DisplayName("Find by active status true list")
    class findByActiveStatus{
        @Test
        @DisplayName("Should return list of active inventories when active status is true")
        void shouldReturnListOfActiveInventoriesWhenActiveStatusIsTrue(){
            List<Inventory> activeInventories = Arrays.asList(inventory, anotherInventory);
            Boolean activeStatus = true;
            when(inventoryRepository.findByActiveStatus(activeStatus)).thenReturn(activeInventories);

            List<InventorySummaryDTO> result = inventoryService.findByActiveStatusList(activeStatus);

            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).quantityAvailable()).isEqualTo(100);
            assertThat(result.get(0).activeStatus()).isTrue();
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(1).quantityAvailable()).isEqualTo(200);
            verify(inventoryRepository, times(1)).findByActiveStatus(activeStatus);
        }

        @Test
        @DisplayName("Should return list of inactive inventories when active status is false")
        void shouldReturnListOfInactiveInventoriesWhenActiveStatusIsFalse() {

            Boolean activeStatus = false;
            inventory.setActiveStatus(activeStatus);

            List<Inventory> inactiveInventories = Collections.singletonList(inventory);

            when(inventoryRepository.findByActiveStatus(activeStatus)).thenReturn(inactiveInventories);

            List<InventorySummaryDTO> result = inventoryService.findByActiveStatusList(activeStatus);

            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).activeStatus()).isFalse();
            verify(inventoryRepository, times(1)).findByActiveStatus(activeStatus);
        }
        @Test
        @DisplayName("Should return empty list when no inventories match active status")
        void shouldReturnEmptyListWhenNoInventoriesMatchActiveStatus() {

            Boolean activeStatus = true;
            List<Inventory> emptyInventories = Collections.emptyList();

            when(inventoryRepository.findByActiveStatus(activeStatus)).thenReturn(emptyInventories);

            List<InventorySummaryDTO> result = inventoryService.findByActiveStatusList(activeStatus);

            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(inventoryRepository, times(1)).findByActiveStatus(activeStatus);
        }
    }
    @Nested
    @DisplayName("Find by alert low stock list")
    class findByAlertLowStock{
        @Test
        @DisplayName("Should return list of inventories with low stock alert when inventories exist")
        void shouldReturnListOfInventoriesWithLowStockAlertWhenInventoriesExist(){

            inventory.setQuantityAvailable(5);
            inventory.setAlertLowStock(true);
            anotherInventory.setQuantityAvailable(5);
            anotherInventory.setAlertLowStock(true);

            List<Inventory> lowStockInventories = Arrays.asList(inventory, anotherInventory);

            when(inventoryRepository.findActiveInventoriesWithLowStock()).thenReturn(lowStockInventories);

            List<InventorySummaryDTO> result = inventoryService.findByAlertLowStockList();

            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).quantityAvailable()).isEqualTo(5);
            assertThat(result.get(0).activeStatus()).isTrue();
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(1).quantityAvailable()).isEqualTo(5);
            assertThat(result.get(1).activeStatus()).isTrue();
            verify(inventoryRepository, times(1)).findActiveInventoriesWithLowStock();
        }

        @Test
        @DisplayName("Should return empty list when no inventories have low stock alert")
        void shouldReturnEmptyListWhenNoInventoriesHaveLowStockAlert() {

            List<Inventory> emptyInventories = Collections.emptyList();

            when(inventoryRepository.findActiveInventoriesWithLowStock()).thenReturn(emptyInventories);

            List<InventorySummaryDTO> result = inventoryService.findByAlertLowStockList();

            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(inventoryRepository, times(1)).findActiveInventoriesWithLowStock();
        }
    }
    @Nested
    @DisplayName("check book availability")
    class checkBookAvailability{
        @Test
        @DisplayName("Should return available status when book has sufficient stock")
        void shouldReturnAvailableStatusWhenBookHasSufficientStock(){
            Long bookId = 1L;
            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            CheckAvailabilityResponseDTO result = inventoryService.checkBookAvailability(bookId);

            assertThat(result).isNotNull();
            assertThat(result.bookId()).isEqualTo(bookId);
            assertThat(result.isAvailable()).isTrue();
            assertThat(result.status()).isEqualTo(AvailabilityStatus.AVAILABLE);
            assertThat(result.quantityAvailable()).isEqualTo(100);
            verify(inventoryRepository, times(1)).findByBookId(bookId);
        }

        @Test
        @DisplayName("Should return few units status when book quantity is below minimum")
        void shouldReturnFewUnitsStatusWhenBookQuantityIsBelowMinimum() {
            Long bookId = 1L;
            inventory.setQuantityAvailable(10);
            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            CheckAvailabilityResponseDTO result = inventoryService.checkBookAvailability(bookId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.bookId()).isEqualTo(bookId);
            assertThat(result.isAvailable()).isTrue();
            assertThat(result.quantityAvailable()).isEqualTo(10);
            assertThat(result.status()).isEqualTo(AvailabilityStatus.FEW_UNITS);
            verify(inventoryRepository, times(1)).findByBookId(bookId);
        }

        @Test
        @DisplayName("Should return unavailable when book has zero stock")
        void shouldReturnUnavailableWhenBookHasZeroStock() {
            Long bookId = 1L;
            inventory.setQuantityAvailable(0);
            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            CheckAvailabilityResponseDTO result = inventoryService.checkBookAvailability(bookId);

            assertThat(result).isNotNull();
            assertThat(result.bookId()).isEqualTo(bookId);
            assertThat(result.isAvailable()).isFalse();
            assertThat(result.quantityAvailable()).isZero();
            assertThat(result.status()).isEqualTo(AvailabilityStatus.UNAVAILABLE);
            verify(inventoryRepository, times(1)).findByBookId(bookId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory() {

            Long bookId = 999L;

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.checkBookAvailability(bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
        }
    }
    @Nested
    @DisplayName("Register Sale Tests")
    class registerSale{
        @Test
        @DisplayName("Should register sale successfully when sufficient stock is available")
        void shouldRegisterSaleSuccessfullyWhenSufficientStockIsAvailable(){
            Long bookId = 1L;
            inventory.setQuantityAvailable(50);
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(10, MovementType.EXIT);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.registerSale(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(40);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(50);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(40);
            assertThat(capturedMovement.getAffectedQuantity()).isEqualTo(-10);
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.EXIT);
        }

        @Test
        @DisplayName("Should set alert low stock to true when quantity after sale is below or equal to minimum stock")
        void shouldSetAlertLowStockToTrueWhenQuantityAfterSaleIsBelowOrEqualToMinimumStock() {
            Long bookId = 2L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    15,
                    MovementType.EXIT
            );
            anotherInventory.setStockMin(5);
            anotherInventory.setQuantityAvailable(20);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(anotherInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(anotherInventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.registerSale(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(anotherInventory.getQuantityAvailable()).isEqualTo(5);
            assertThat(anotherInventory.getAlertLowStock()).isTrue();

            verify(inventoryRepository, times(1)).save(anotherInventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when quantity to sell exceeds available stock")
        void shouldThrowInsufficientStockExceptionWhenQuantityToSellExceedsAvailableStock() {
            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    100,
                    MovementType.EXIT
            );
            inventory.setQuantityAvailable(90);
            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            assertThatThrownBy(() -> inventoryService.registerSale(updateStockDTO, bookId))
                    .isInstanceOf(InsufficientStockException.class)
                    .hasMessageContaining("Insufficient Stock");

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory() {
            Long bookId = 999L;

            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    10,
                    MovementType.EXIT
            );

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.registerSale(updateStockDTO, bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
    @Nested
    @DisplayName("Register Entry Tests")
    class registerEntry{
        @Test
        @DisplayName("Should register entry successfully and increase stock")
        void shouldRegisterEntrySuccessfullyAndIncreaseStock(){

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    50,
                    MovementType.ENTRY
            );
            inventory.setQuantityAvailable(20);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.registerEntry(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(70);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(20);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(70);
            assertThat(capturedMovement.getAffectedQuantity()).isEqualTo(50);
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.ENTRY);
        }

        @Test
        @DisplayName("Should set alert low stock to false when quantity after entry exceeds minimum stock")
        void shouldSetAlertLowStockToFalseWhenQuantityAfterEntryExceedsMinimumStock() {

            Long bookId = 2L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    30,
                    MovementType.ENTRY
            );
            anotherInventory.setQuantityAvailable(5);
            anotherInventory.setStockMin(10);
            anotherInventory.setAlertLowStock(true);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(anotherInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(anotherInventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.registerEntry(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(anotherInventory.getQuantityAvailable()).isEqualTo(35);
            assertThat(anotherInventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).save(anotherInventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should keep alert low stock true when quantity after entry is still below or equal to minimum stock")
        void shouldKeepAlertLowStockTrueWhenQuantityAfterEntryIsStillBelowOrEqualToMinimumStock(){

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    3,
                    MovementType.ENTRY
            );
            inventory.setQuantityAvailable(5);
            inventory.setStockMin(10);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.registerEntry(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(8);
            assertThat(inventory.getAlertLowStock()).isTrue();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory(){

            Long bookId = 999L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    20,
                    MovementType.ENTRY
            );

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.registerEntry(updateStockDTO, bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
    @Nested
    @DisplayName("Positive Adjustment Tests")
    class positiveAdjustment{
        @Test
        @DisplayName("Should apply positive adjustment successfully and increase stock")
        void shouldApplyPositiveAdjustmentSuccessfullyAndIncreaseStock(){

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    15,
                    MovementType.POSITIVE_ADJUSTMENT
            );
            inventory.setQuantityAvailable(25);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.positiveAdjustment(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(40);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(25);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(40);
            assertThat(capturedMovement.getAffectedQuantity()).isEqualTo(15);
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.POSITIVE_ADJUSTMENT);
        }

        @Test
        @DisplayName("Should set alert low stock to false when quantity after positive adjustment exceeds minimum stock")
        void shouldSetAlertLowStockToFalseWhenQuantityAfterPositiveAdjustmentExceedsMinimumStock() {

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    20,
                    MovementType.POSITIVE_ADJUSTMENT
            );
            inventory.setQuantityAvailable(8);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.positiveAdjustment(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(28);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should keep alert low stock true when quantity after positive adjustment is still below or equal to minimum stock")
        void shouldKeepAlertLowStockTrueWhenQuantityAfterPositiveAdjustmentIsStillBelowOrEqualToMinimumStock() {

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    2,
                    MovementType.POSITIVE_ADJUSTMENT
            );
            inventory.setQuantityAvailable(6);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(true);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.positiveAdjustment(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(8);
            assertThat(inventory.getAlertLowStock()).isTrue();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory(){

            Long bookId = 999L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    10,
                    MovementType.POSITIVE_ADJUSTMENT
            );

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.positiveAdjustment(updateStockDTO, bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
    @Nested
    @DisplayName("Negative Adjustment Tests")
    class negativeAdjustment{
        @Test
        @DisplayName("Should apply negative adjustment successfully and decrease stock")
        void shouldApplyNegativeAdjustmentSuccessfullyAndDecreaseStock(){

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    10,
                    MovementType.NEGATIVE_ADJUSTMENT
            );

            inventory.setQuantityAvailable(30);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.negativeAdjustment(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(20);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(30);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(20);
            assertThat(capturedMovement.getAffectedQuantity()).isEqualTo(-10);
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.NEGATIVE_ADJUSTMENT);
        }
        @Test
        @DisplayName("Should set alert low stock to true when quantity after negative adjustment is below or equal to minimum stock")
        void shouldSetAlertLowStockToTrueWhenQuantityAfterNegativeAdjustmentIsBelowOrEqualToMinimumStock() {

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    15,
                    MovementType.NEGATIVE_ADJUSTMENT
            );

            inventory.setQuantityAvailable(20);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.negativeAdjustment(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isEqualTo(5);
            assertThat(inventory.getAlertLowStock()).isTrue();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }
        @Test
        @DisplayName("Should throw InvalidAdjustmentException when negative adjustment would result in negative stock")
        void shouldThrowInvalidAdjustmentExceptionWhenNegativeAdjustmentWouldResultInNegativeStock() {

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    50,
                    MovementType.NEGATIVE_ADJUSTMENT
            );

            inventory.setQuantityAvailable(30);
            inventory.setStockMin(10);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            assertThatThrownBy(() -> inventoryService.negativeAdjustment(updateStockDTO, bookId))
                    .isInstanceOf(InvalidAdjustmentException.class)
                    .hasMessageContaining("The adjustment would leave negative stock.");

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
        @Test
        @DisplayName("Should allow adjustment to exactly zero stock")
        void shouldAllowAdjustmentToExactlyZeroStock() {

            Long bookId = 1L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    15,
                    MovementType.NEGATIVE_ADJUSTMENT
            );

            inventory.setQuantityAvailable(15);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.negativeAdjustment(updateStockDTO, bookId);

            assertThat(result).isNotNull();
            assertThat(inventory.getQuantityAvailable()).isZero();
            assertThat(inventory.getAlertLowStock()).isTrue();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory() {

            Long bookId = 999L;
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    10,
                    MovementType.NEGATIVE_ADJUSTMENT
            );

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.negativeAdjustment(updateStockDTO, bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
    @Nested
    @DisplayName("create")
    class create{
        @Test
        @DisplayName("Should create inventory successfully when book does not have existing inventory")
        void shouldCreateInventorySuccessfullyWhenBookDoesNotHaveExistingInventory() {

            Long bookId = 1L;

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.create(createInventoryDTO);

            assertThat(result).isNotNull();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(any(Inventory.class));

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isZero();
            assertThat(capturedMovement.getAffectedQuantity()).isEqualTo(inventory.getQuantityAvailable());
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(inventory.getQuantityAvailable());
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.INITIAL_INVENTORY);
        }

        @Test
        @DisplayName("Should create inventory movement with initial inventory type")
        void shouldCreateInventoryMovementWithInitialInventoryType() {

            Long bookId = 1L;

            inventory.setQuantityAvailable(30);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventorySummaryDTO result = inventoryService.create(createInventoryDTO);

            assertThat(result).isNotNull();

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.INITIAL_INVENTORY);
            assertThat(capturedMovement.getDescription()).isEqualTo(MovementType.INITIAL_INVENTORY.getDescription());
        }

        @Test
        @DisplayName("Should throw DuplicateEntityException when inventory already exists for book")
        void shouldThrowDuplicateEntityExceptionWhenInventoryAlreadyExistsForBook() {

            Long bookId = 1L;

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            assertThatThrownBy(() -> inventoryService.create(createInventoryDTO))
                    .isInstanceOf(DuplicateEntityException.class)
                    .hasMessageContaining("Inventory")
                    .hasMessageContaining("BookId")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
    @Nested
    @DisplayName("Release Reservation Tests")
    class releaseReservation{
        @Test
        @DisplayName("Should release reservation successfully when sufficient reserved stock exists")
        void shouldReleaseReservationSuccessfullyWhenSufficientReservedStockExists() {

            Long bookId = 1L;
            Integer quantity = 5;

            inventory.setQuantityAvailable(20);
            inventory.setQuantityReserved(10);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.releaseReservation(bookId, quantity);

            assertThat(inventory.getQuantityAvailable()).isEqualTo(25);
            assertThat(inventory.getQuantityReserved()).isEqualTo(5);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(20);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(25);
            assertThat(capturedMovement.getAffectedQuantity()).isEqualTo(5);
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.RELEASE_RESERVE);
        }

        @Test
        @DisplayName("Should set alert low stock to false when quantity after release exceeds minimum stock")
        void shouldSetAlertLowStockToFalseWhenQuantityAfterReleaseExceedsMinimumStock() {

            Long bookId = 1L;
            Integer quantity = 10;

            inventory.setQuantityAvailable(5);
            inventory.setQuantityReserved(15);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(true);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.releaseReservation(bookId, quantity);

            assertThat(inventory.getQuantityAvailable()).isEqualTo(15);
            assertThat(inventory.getQuantityReserved()).isEqualTo(5);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw InsufficientReservedStockException when reserved quantity is less than release quantity")
        void shouldThrowInsufficientReservedStockExceptionWhenReservedQuantityIsLessThanReleaseQuantity() {

            Long bookId = 1L;
            Integer quantity = 20;

            inventory.setQuantityAvailable(30);
            inventory.setQuantityReserved(10);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            assertThatThrownBy(() -> inventoryService.releaseReservation(bookId, quantity))
                    .isInstanceOf(InsufficientReservedStockException.class)
                    .hasMessageContaining("Not enough reserved stock to release");

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should release exact reserved quantity successfully")
        void shouldReleaseExactReservedQuantitySuccessfully() {

            Long bookId = 1L;
            Integer quantity = 10;

            inventory.setQuantityAvailable(15);
            inventory.setQuantityReserved(10);
            inventory.setStockMin(5);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.releaseReservation(bookId, quantity);

            assertThat(inventory.getQuantityAvailable()).isEqualTo(25);
            assertThat(inventory.getQuantityReserved()).isZero();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory() {

            Long bookId = 999L;
            Integer quantity = 5;

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.releaseReservation(bookId, quantity))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
    @Nested
    @DisplayName("Reserve Stock Tests")
    class reserveStock{
        @Test
        @DisplayName("Should reserve stock successfully when sufficient available stock exists")
        void shouldReserveStockSuccessfullyWhenSufficientAvailableStockExists() {

            Long bookId = 1L;
            Integer quantity = 5;

            inventory.setQuantityAvailable(30);
            inventory.setQuantityReserved(5);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.reserveStock(bookId, quantity);

            assertThat(inventory.getQuantityAvailable()).isEqualTo(30);
            assertThat(inventory.getQuantityReserved()).isEqualTo(10);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(30);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(25);
            assertThat(capturedMovement.getAffectedQuantity()).isEqualTo(-5);
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.RESERVE);
        }

        @Test
        @DisplayName("Should set alert low stock to true when quantity after reservation is below or equal to minimum stock")
        void shouldSetAlertLowStockToTrueWhenQuantityAfterReservationIsBelowOrEqualToMinimumStock() {

            Long bookId = 1L;
            Integer quantity = 15;

            inventory.setQuantityAvailable(20);
            inventory.setQuantityReserved(0);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.reserveStock(bookId, quantity);

            assertThat(inventory.getQuantityAvailable()).isEqualTo(20);
            assertThat(inventory.getQuantityReserved()).isEqualTo(15);
            assertThat(inventory.getAlertLowStock()).isTrue();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when available stock is less than reserve quantity")
        void shouldThrowInsufficientStockExceptionWhenAvailableStockIsLessThanReserveQuantity() {

            Long bookId = 1L;
            Integer quantity = 50;

            inventory.setQuantityAvailable(20);
            inventory.setQuantityReserved(5);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            assertThatThrownBy(() -> inventoryService.reserveStock(bookId, quantity))
                    .isInstanceOf(InsufficientStockException.class)
                    .hasMessageContaining("Insufficient stock");

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should reserve exact available quantity successfully")
        void shouldReserveExactAvailableQuantitySuccessfully() {

            Long bookId = 1L;
            Integer quantity = 25;

            inventory.setQuantityAvailable(25);
            inventory.setQuantityReserved(0);
            inventory.setStockMin(10);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.reserveStock(bookId, quantity);

            assertThat(inventory.getQuantityAvailable()).isEqualTo(25);
            assertThat(inventory.getQuantityReserved()).isEqualTo(25);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityAfter()).isZero();

            verify(inventoryRepository, times(1)).save(inventory);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory() {

            Long bookId = 999L;
            Integer quantity = 5;

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.reserveStock(bookId, quantity))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
    @Nested
    @DisplayName("Update Thresholds Tests")
    class updateThresholds{
        @Test
        @DisplayName("Should update thresholds successfully when minimum is less than maximum")
        void shouldUpdateThresholdsSuccessfullyWhenMinimumIsLessThanMaximum() {

            Long bookId = 1L;
            Integer stockMin = 15;
            Integer stockMax = 100;

            inventory.setQuantityAvailable(50);
            inventory.setStockMin(10);
            inventory.setStockMax(80);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventoryResponseDTO result = inventoryService.updateThresholds(bookId, stockMin, stockMax);

            assertThat(result).isNotNull();
            assertThat(inventory.getStockMin()).isEqualTo(15);
            assertThat(inventory.getStockMax()).isEqualTo(100);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(50);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(50);
            assertThat(capturedMovement.getAffectedQuantity()).isZero();
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.UPDATE_THRESHOLD);
        }

        @Test
        @DisplayName("Should set alert low stock to true when available quantity is below or equal to new minimum threshold")
        void shouldSetAlertLowStockToTrueWhenAvailableQuantityIsBelowOrEqualToNewMinimumThreshold() {

            Long bookId = 1L;
            Integer stockMin = 30;
            Integer stockMax = 100;

            inventory.setQuantityAvailable(20);
            inventory.setStockMin(10);
            inventory.setStockMax(80);
            inventory.setAlertLowStock(false);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventoryResponseDTO result = inventoryService.updateThresholds(bookId, stockMin, stockMax);

            assertThat(result).isNotNull();
            assertThat(inventory.getStockMin()).isEqualTo(30);
            assertThat(inventory.getStockMax()).isEqualTo(100);
            assertThat(inventory.getAlertLowStock()).isTrue();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should set alert low stock to false when available quantity exceeds new minimum threshold")
        void shouldSetAlertLowStockToFalseWhenAvailableQuantityExceedsNewMinimumThreshold() {

            Long bookId = 1L;
            Integer stockMin = 10;
            Integer stockMax = 80;

            inventory.setQuantityAvailable(50);
            inventory.setStockMin(60);
            inventory.setStockMax(100);
            inventory.setAlertLowStock(true);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            InventoryResponseDTO result = inventoryService.updateThresholds(bookId, stockMin, stockMax);

            assertThat(result).isNotNull();
            assertThat(inventory.getStockMin()).isEqualTo(10);
            assertThat(inventory.getAlertLowStock()).isFalse();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw InvalidStockThresholdException when minimum stock is greater than maximum stock")
        void shouldThrowInvalidStockThresholdExceptionWhenMinimumStockIsGreaterThanMaximumStock() {

            Long bookId = 1L;
            Integer stockMin = 100;
            Integer stockMax = 50;

            inventory.setQuantityAvailable(30);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            assertThatThrownBy(() -> inventoryService.updateThresholds(bookId, stockMin, stockMax))
                    .isInstanceOf(InvalidStockThresholdException.class)
                    .hasMessageContaining("Minimum stock cannot be greater than or equal to the maximum stock");

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw InvalidStockThresholdException when minimum stock equals maximum stock")
        void shouldThrowInvalidStockThresholdExceptionWhenMinimumStockEqualsMaximumStock() {

            Long bookId = 1L;
            Integer stockMin = 50;
            Integer stockMax = 50;

            inventory.setQuantityAvailable(30);

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));

            assertThatThrownBy(() -> inventoryService.updateThresholds(bookId, stockMin, stockMax))
                    .isInstanceOf(InvalidStockThresholdException.class)
                    .hasMessageContaining("Minimum stock cannot be greater than or equal to the maximum stock");

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book does not exist in inventory")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExistInInventory() {

            Long bookId = 999L;
            Integer stockMin = 10;
            Integer stockMax = 100;

            when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.updateThresholds(bookId, stockMin, stockMax))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(bookId.toString());

            verify(inventoryRepository, times(1)).findByBookId(bookId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }

    @Nested
    @DisplayName("Disable By Id Tests")
    class disableById {
        @Test
        @DisplayName("Should disable inventory successfully when inventory exists")
        void shouldDisableInventorySuccessfullyWhenInventoryExists() {

            Long inventoryId = 1L;

            inventory.setId(inventoryId);
            inventory.setQuantityAvailable(50);
            inventory.setActiveStatus(true);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.disableById(inventoryId);

            assertThat(inventory.getActiveStatus()).isFalse();

            verify(inventoryRepository, times(1)).findById(inventoryId);
            verify(inventoryRepository, times(1)).save(inventory);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getQuantityBefore()).isEqualTo(50);
            assertThat(capturedMovement.getQuantityAfter()).isEqualTo(50);
            assertThat(capturedMovement.getAffectedQuantity()).isZero();
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.DISABLE);
        }

        @Test
        @DisplayName("Should create movement with disable type when disabling inventory")
        void shouldCreateMovementWithDisableTypeWhenDisablingInventory() {

            Long inventoryId = 1L;

            inventory.setId(inventoryId);
            inventory.setQuantityAvailable(30);
            inventory.setActiveStatus(true);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.disableById(inventoryId);

            ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
            verify(inventoryMovementRepository, times(1)).save(movementCaptor.capture());

            InventoryMovement capturedMovement = movementCaptor.getValue();
            assertThat(capturedMovement.getMovementType()).isEqualTo(MovementType.DISABLE);
            assertThat(capturedMovement.getDescription()).isEqualTo(MovementType.DISABLE.getDescription());
        }

        @Test
        @DisplayName("Should disable already inactive inventory without errors")
        void shouldDisableAlreadyInactiveInventoryWithoutErrors() {

            Long inventoryId = 1L;

            inventory.setId(inventoryId);
            inventory.setQuantityAvailable(20);
            inventory.setActiveStatus(false);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(InventoryMovement.builder().build());

            inventoryService.disableById(inventoryId);

            assertThat(inventory.getActiveStatus()).isFalse();

            verify(inventoryRepository, times(1)).save(inventory);
            verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when inventory does not exist")
        void shouldThrowResourceNotFoundExceptionWhenInventoryDoesNotExist() {

            Long inventoryId = 999L;

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.disableById(inventoryId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Inventory")
                    .hasMessageContaining("Id")
                    .hasMessageContaining(inventoryId.toString());

            verify(inventoryRepository, times(1)).findById(inventoryId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }
    }
}
