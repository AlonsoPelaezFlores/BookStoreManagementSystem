package com.bookstore.management.inventory.service;

import com.bookstore.management.book.model.Book;
import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.mapper.InventoryMovementMapper;
import com.bookstore.management.inventory.model.Inventory;
import com.bookstore.management.inventory.model.InventoryMovement;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.repository.InventoryMovementRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class InventoryMovementServiceImplTest {
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @InjectMocks
    private InventoryMovementServiceImpl inventoryMovementServiceImpl;
    @Spy
    private InventoryMovementMapper inventoryMovementMapper =
            Mappers.getMapper(InventoryMovementMapper.class);

    private Book book;
    private Inventory inventory;
    private InventoryMovement movement;
    private InventoryMovement movement2;
    private InventoryMovement movement3;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp(){
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .book(book)
                .quantityAvailable(50)
                .quantityReserved(5)
                .stockMin(10)
                .stockMax(100)
                .activeStatus(true)
                .build();

        movement = InventoryMovement.builder()
                .id(1L)
                .inventory(inventory)
                .affectedQuantity(10)
                .quantityBefore(40)
                .quantityAfter(50)
                .movementType(MovementType.ENTRY)
                .description(MovementType.ENTRY.getDescription())
                .createBy("SYSTEM")
                .createdAt(LocalDateTime.now())
                .build();

        movement2 = InventoryMovement.builder()
                .id(2L)
                .inventory(inventory)
                .affectedQuantity(-5)
                .quantityBefore(50)
                .quantityAfter(45)
                .movementType(MovementType.EXIT)
                .description(MovementType.EXIT.getDescription())
                .createBy("SYSTEM")
                .createdAt(LocalDateTime.now())
                .build();

        movement3 = InventoryMovement.builder()
                .id(3L)
                .inventory(inventory)
                .affectedQuantity(3)
                .quantityBefore(45)
                .quantityAfter(48)
                .movementType(MovementType.POSITIVE_ADJUSTMENT)
                .description(MovementType.POSITIVE_ADJUSTMENT.getDescription())
                .createBy("SYSTEM")
                .createdAt(LocalDateTime.now())
                .build();
        defaultPageable = PageRequest.of(0, 100, Sort.by("createdAt").descending());
    }
    @Nested
    @DisplayName("Find by inventory id tests")
    class FindByInventoryId{

        @Test
        @DisplayName("Should return paginated movements for valid inventory ID")
        void shouldReturnPaginatedMovementsForValidInventoryId(){
            Long inventoryId = 1L;
            List<InventoryMovement> movements = Arrays.asList(movement,movement2);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, defaultPageable, 2);

            when(inventoryMovementRepository.findAllByInventoryId(inventoryId, defaultPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(any(InventoryMovement.class)))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findAllByInventoryId(inventoryId, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);

            verify(inventoryMovementRepository).findAllByInventoryId(inventoryId, defaultPageable);
            verify(inventoryMovementMapper, times(2)).toInventoryMovementResponseDTO(any(InventoryMovement.class));

        }

        @Test
        @DisplayName("Should return empty page when no movements exist for inventory")
        void shouldReturnEmptyPageWhenNoMovementsExistForInventory(){

            Long inventoryId = 999L;
            Page<InventoryMovement> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);

            when(inventoryMovementRepository.findAllByInventoryId(inventoryId, defaultPageable))
                    .thenReturn(emptyPage);

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findAllByInventoryId(inventoryId, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getTotalPages()).isZero();

            verify(inventoryMovementRepository).findAllByInventoryId(inventoryId, defaultPageable);
            verify(inventoryMovementMapper, never()).toInventoryMovementResponseDTO(any());
        }

        @Test
        @DisplayName("Should handle custom page size and sorting")
        void shouldHandleCustomPageSizeAndSorting(){
            Long inventoryId = 100L;
            Pageable customPageable = PageRequest.of(1, 5, Sort.by("affectedQuantity").ascending());
            List<InventoryMovement> movements = Collections.singletonList(movement3);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, customPageable, 6);

            when(inventoryMovementRepository.findAllByInventoryId(inventoryId, customPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(movement3))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findAllByInventoryId(inventoryId, customPageable);

            assertThat(result).isNotNull();
            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(2);

            verify(inventoryMovementRepository).findAllByInventoryId(inventoryId, customPageable);
        }
    }

    @Nested
    @DisplayName("find by movement type tests")
    class findByMovementTypeTests{
        @Test
        @DisplayName("Should return movements filtered by entry type")
        void shouldReturnMovementsFilteredByEntryType(){
            MovementType movementType = MovementType.ENTRY;
            List<InventoryMovement> movements = Collections.singletonList(movement);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, defaultPageable, 1);

            when(inventoryMovementRepository.findByMovementType(movementType, defaultPageable))
                    .thenReturn(movementPage);

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findByMovementType(movementType, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).movementType()).isEqualTo(MovementType.ENTRY);

            verify(inventoryMovementRepository).findByMovementType(movementType, defaultPageable);
        }
        @Test
        @DisplayName("Should return movements fitered by EXIT type")
        void shouldReturnMovementsFilteredByExitType(){
            MovementType movementType = MovementType.EXIT;
            List<InventoryMovement> movements = Collections.singletonList(movement2);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, defaultPageable, 1);

            when(inventoryMovementRepository.findByMovementType(movementType, defaultPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(movement2))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findByMovementType(movementType, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(inventoryMovementRepository).findByMovementType(movementType, defaultPageable);
        }
        @Test
        @DisplayName("Should return empty page when no movements of specified type exist")
        void shouldReturnEmptyWhenNoMovementsOfTypeExist(){
            MovementType movementType = MovementType.RESERVE;
            Page<InventoryMovement> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);

            when(inventoryMovementRepository.findByMovementType(movementType, defaultPageable))
                    .thenReturn(emptyPage);

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findByMovementType(movementType, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();

            verify(inventoryMovementRepository).findByMovementType(movementType, defaultPageable);
        }
        @Test
        @DisplayName("Should handle pagination with multiple movement types")
        void shouldHandlePaginationWithMultipleMovementTypes() {
            MovementType movementType = MovementType.POSITIVE_ADJUSTMENT;
            Pageable customPageable = PageRequest.of(0, 10);
            List<InventoryMovement> movements = Collections.singletonList(movement3);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, customPageable, 1);

            when(inventoryMovementRepository.findByMovementType(movementType, customPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(movement3))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findByMovementType(movementType, customPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(inventoryMovementRepository).findByMovementType(movementType, customPageable);
        }
    }

    @Nested
    @DisplayName("find by date range Tests")
    class FindByDateRangeTests {

        @Test
        @DisplayName("Should return movements within valid date range")
        void shouldReturnMovementsWithinValidDateRange() {
            LocalDateTime startDate = LocalDateTime.now().minusHours(2);
            LocalDateTime endDate = LocalDateTime.now().plusHours(1);
            List<InventoryMovement> movements = Arrays.asList(movement, movement2, movement3);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, defaultPageable, 3);

            when(inventoryMovementRepository.findByCreatedAtBetween(startDate, endDate, defaultPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(any(InventoryMovement.class)))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findByDateRange(startDate, endDate, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(3);
            verify(inventoryMovementRepository).findByCreatedAtBetween(startDate, endDate, defaultPageable);
        }

        @Test
        @DisplayName("Should throw exception when start date is null")
        void shouldThrowExceptionWhenStartDateIsNull() {
            LocalDateTime endDate = LocalDateTime.now();

            assertThatThrownBy(() -> inventoryMovementServiceImpl.findByDateRange(null, endDate, defaultPageable))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Dates cannot be null");

            verify(inventoryMovementRepository, never()).findByCreatedAtBetween(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw exception when end date is null")
        void shouldThrowExceptionWhenEndDateIsNull() {
            LocalDateTime startDate = LocalDateTime.now();

            assertThatThrownBy(() -> inventoryMovementServiceImpl.findByDateRange(startDate, null, defaultPageable))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Dates cannot be null");

            verify(inventoryMovementRepository, never()).findByCreatedAtBetween(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw exception when both dates are null")
        void shouldThrowExceptionWhenBothDatesAreNull() {
            assertThatThrownBy(() -> inventoryMovementServiceImpl.findByDateRange(null, null, defaultPageable))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Dates cannot be null");

            verify(inventoryMovementRepository, never()).findByCreatedAtBetween(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw exception when start date is after end date")
        void shouldThrowExceptionWhenStartDateAfterEndDate() {
            LocalDateTime startDate = LocalDateTime.now().plusHours(1);
            LocalDateTime endDate = LocalDateTime.now().minusHours(1);

            assertThatThrownBy(() -> inventoryMovementServiceImpl.findByDateRange(startDate, endDate, defaultPageable))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Start date must be before or equal to end date");

            verify(inventoryMovementRepository, never()).findByCreatedAtBetween(any(), any(), any());
        }

        @Test
        @DisplayName("Should accept same start and end date")
        void shouldAcceptSameStartAndEndDate() {
            LocalDateTime sameDate = LocalDateTime.now();
            List<InventoryMovement> movements = Collections.singletonList(movement);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, defaultPageable, 1);

            when(inventoryMovementRepository.findByCreatedAtBetween(sameDate, sameDate, defaultPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(movement))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findByDateRange(sameDate, sameDate, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(inventoryMovementRepository).findByCreatedAtBetween(sameDate, sameDate, defaultPageable);
        }

        @Test
        @DisplayName("Should return empty page when no movements in date range")
        void shouldReturnEmptyPageWhenNoMovementsInDateRange() {
            LocalDateTime startDate = LocalDateTime.now().minusDays(10);
            LocalDateTime endDate = LocalDateTime.now().minusDays(9);
            Page<InventoryMovement> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);

            when(inventoryMovementRepository.findByCreatedAtBetween(startDate, endDate, defaultPageable))
                    .thenReturn(emptyPage);

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findByDateRange(startDate, endDate, defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            verify(inventoryMovementRepository).findByCreatedAtBetween(startDate, endDate, defaultPageable);
        }
    }

    @Nested
    @DisplayName("find recent movements Tests")
    class FindRecentMovementsTests {

        @Test
        @DisplayName("Should return movements with provided pageable")
        void shouldReturnMovementsWithProvidedPageable() {
            List<InventoryMovement> movements = Arrays.asList(movement, movement2, movement3);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, defaultPageable, 3);

            when(inventoryMovementRepository.findAllBy(defaultPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(any(InventoryMovement.class)))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findRecentMovements(defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            verify(inventoryMovementRepository).findAllBy(defaultPageable);
        }

        @Test
        @DisplayName("Should return empty page when no movements exist")
        void shouldReturnEmptyPageWhenNoMovementsExist() {
            Page<InventoryMovement> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);

            when(inventoryMovementRepository.findAllBy(defaultPageable))
                    .thenReturn(emptyPage);

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findRecentMovements(defaultPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            verify(inventoryMovementRepository).findAllBy(defaultPageable);
        }

        @Test
        @DisplayName("Should handle different page sizes correctly")
        void shouldHandleDifferentPageSizes() {
            Pageable smallPageable = PageRequest.of(0, 5);
            List<InventoryMovement> movements = Arrays.asList(movement, movement2);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, smallPageable, 10);

            when(inventoryMovementRepository.findAllBy(smallPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(any(InventoryMovement.class)))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findRecentMovements(smallPageable);

            assertThat(result).isNotNull();
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(2);
            verify(inventoryMovementRepository).findAllBy(smallPageable);
        }

        @Test
        @DisplayName("Should respect pageable sorting without modification")
        void shouldRespectPageableSortingWithoutModification() {
            Pageable customSortedPageable = PageRequest.of(0, 10, Sort.by("affectedQuantity").ascending());
            List<InventoryMovement> movements = Arrays.asList(movement3, movement2, movement);
            Page<InventoryMovement> movementPage = new PageImpl<>(movements, customSortedPageable, 3);

            when(inventoryMovementRepository.findAllBy(customSortedPageable))
                    .thenReturn(movementPage);
            when(inventoryMovementMapper.toInventoryMovementResponseDTO(any(InventoryMovement.class)))
                    .thenReturn(mock(InventoryMovementResponseDTO.class));

            Page<InventoryMovementResponseDTO> result = inventoryMovementServiceImpl
                    .findRecentMovements(customSortedPageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            verify(inventoryMovementRepository).findAllBy(customSortedPageable);
        }
    }
}
