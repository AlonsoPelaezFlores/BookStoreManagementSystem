package com.bookstore.management.inventory.controller;

import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.inventory.dto.*;
import com.bookstore.management.inventory.model.AvailabilityStatus;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.service.InventoryServiceImpl;
import com.bookstore.management.shared.exception.custom.*;
import com.bookstore.management.shared.exception.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryServiceImpl inventoryServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Get All Tests")
    class GetAll {

        @Test
        @DisplayName("Should return list of inventory summaries when inventories exist")
        void shouldReturnListOfInventorySummariesWhenInventoriesExist() throws Exception {
            BookSummaryDTO bookSummary1 = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            BookSummaryDTO bookSummary2 = new BookSummaryDTO(2L, "Book 2", "ISBN2", "Author 2");

            InventorySummaryDTO inventory1 = new InventorySummaryDTO(1L, bookSummary1, 50, true);
            InventorySummaryDTO inventory2 = new InventorySummaryDTO(2L, bookSummary2, 30, true);

            List<InventorySummaryDTO> inventories = Arrays.asList(inventory1, inventory2);

            when(inventoryServiceImpl.findAll()).thenReturn(inventories);

            mockMvc.perform(get("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].quantityAvailable", is(50)))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].quantityAvailable", is(30)));

            verify(inventoryServiceImpl, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no inventories exist")
        void shouldReturnEmptyListWhenNoInventoriesExist() throws Exception {
            when(inventoryServiceImpl.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(inventoryServiceImpl, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Get By Book Id Tests")
    class GetByBookId {

        @Test
        @DisplayName("Should return inventory when book id is valid")
        void shouldReturnInventoryWhenBookIdIsValid() throws Exception {
            Long bookId = 1L;
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventoryResponseDTO inventory = new InventoryResponseDTO(
                    1L, bookSummary, 50, 5, 10, 100, LocalDateTime.now(), true, 45
            );

            when(inventoryServiceImpl.findByBookId(bookId)).thenReturn(inventory);

            mockMvc.perform(get("/api/v1/inventory/book/{bookId}", bookId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.quantityAvailable", is(50)))
                    .andExpect(jsonPath("$.quantityReserved", is(5)));

            verify(inventoryServiceImpl, times(1)).findByBookId(bookId);
        }

        @Test
        @DisplayName("Should return not found when book does not exist")
        void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
            Long bookId = 999L;

            when(inventoryServiceImpl.findByBookId(bookId))
                    .thenThrow(new ResourceNotFoundException("Book", "Id", bookId));

            mockMvc.perform(get("/api/v1/inventory/book/{bookId}", bookId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Book")));

            verify(inventoryServiceImpl, times(1)).findByBookId(bookId);
        }

        @Test
        @DisplayName("Should return bad request when book id is negative")
        void shouldReturnBadRequestWhenBookIdIsNegative() throws Exception {
            mockMvc.perform(get("/api/v1/inventory/book/{bookId}", -1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).findByBookId(any());
        }

        @Test
        @DisplayName("Should return bad request when book id is zero")
        void shouldReturnBadRequestWhenBookIdIsZero() throws Exception {
            mockMvc.perform(get("/api/v1/inventory/book/{bookId}", 0)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).findByBookId(any());
        }
    }

    @Nested
    @DisplayName("Get All By Active Status Tests")
    class GetAllByActiveStatus {

        @Test
        @DisplayName("Should return active inventories when status is true")
        void shouldReturnActiveInventoriesWhenStatusIsTrue() throws Exception {
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO inventory = new InventorySummaryDTO(1L, bookSummary, 50, true);

            when(inventoryServiceImpl.findByActiveStatusList(true))
                    .thenReturn(Collections.singletonList(inventory));

            mockMvc.perform(get("/api/v1/inventory/status")
                            .param("active", "true")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].activeStatus", is(true)));

            verify(inventoryServiceImpl, times(1)).findByActiveStatusList(true);
        }

        @Test
        @DisplayName("Should return inactive inventories when status is false")
        void shouldReturnInactiveInventoriesWhenStatusIsFalse() throws Exception {
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO inventory = new InventorySummaryDTO(1L, bookSummary, 50, false);

            when(inventoryServiceImpl.findByActiveStatusList(false))
                    .thenReturn(Collections.singletonList(inventory));

            mockMvc.perform(get("/api/v1/inventory/status")
                            .param("active", "false")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].activeStatus", is(false)));

            verify(inventoryServiceImpl, times(1)).findByActiveStatusList(false);
        }

        @Test
        @DisplayName("Should use default value true when status parameter is missing")
        void shouldUseDefaultValueTrueWhenStatusParameterIsMissing() throws Exception {
            when(inventoryServiceImpl.findByActiveStatusList(true))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/inventory/status")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(inventoryServiceImpl, times(1)).findByActiveStatusList(true);
        }
    }

    @Nested
    @DisplayName("Get By Alert Low Stock Tests")
    class GetByAlertLowStock {

        @Test
        @DisplayName("Should return inventories with low stock alert")
        void shouldReturnInventoriesWithLowStockAlert() throws Exception {
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO inventory = new InventorySummaryDTO(1L, bookSummary, 5, true);

            when(inventoryServiceImpl.findByAlertLowStockList())
                    .thenReturn(Collections.singletonList(inventory));

            mockMvc.perform(get("/api/v1/inventory/low-stock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].quantityAvailable", is(5)));

            verify(inventoryServiceImpl, times(1)).findByAlertLowStockList();
        }

        @Test
        @DisplayName("Should return empty list when no low stock alerts exist")
        void shouldReturnEmptyListWhenNoLowStockAlertsExist() throws Exception {
            when(inventoryServiceImpl.findByAlertLowStockList())
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/inventory/low-stock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(inventoryServiceImpl, times(1)).findByAlertLowStockList();
        }
    }

    @Nested
    @DisplayName("Get Book Availability Tests")
    class GetBookAvailability {

        @Test
        @DisplayName("Should return availability when book id is valid")
        void shouldReturnAvailabilityWhenBookIdIsValid() throws Exception {
            Long bookId = 1L;
            CheckAvailabilityResponseDTO availability =
                    CheckAvailabilityResponseDTO.builder()
                            .bookId(bookId)
                            .isAvailable(true)
                            .status(AvailabilityStatus.AVAILABLE)
                            .quantityAvailable(50)
                            .build();

            when(inventoryServiceImpl.checkBookAvailability(bookId)).thenReturn(availability);

            mockMvc.perform(get("/api/v1/inventory/book/{bookId}/available", bookId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookId", is(1)))
                    .andExpect(jsonPath("$.isAvailable", is(true)))
                    .andExpect(jsonPath("$.quantityAvailable", is(50)));

            verify(inventoryServiceImpl, times(1)).checkBookAvailability(bookId);
        }

        @Test
        @DisplayName("Should return not found when book does not exist")
        void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
            Long bookId = 999L;

            when(inventoryServiceImpl.checkBookAvailability(bookId))
                    .thenThrow(new ResourceNotFoundException("Book", "Id", bookId));

            mockMvc.perform(get("/api/v1/inventory/book/{bookId}/available", bookId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(inventoryServiceImpl, times(1)).checkBookAvailability(bookId);
        }

        @Test
        @DisplayName("Should return bad request when book id is negative")
        void shouldReturnBadRequestWhenBookIdIsNegative() throws Exception {
            mockMvc.perform(get("/api/v1/inventory/book/{bookId}/available", -1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).checkBookAvailability(any());
        }
    }

    @Nested
    @DisplayName("Register Sale Tests")
    class RegisterSale {

        @Test
        @DisplayName("Should register sale when request is valid")
        void shouldRegisterSaleWhenRequestIsValid() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(10, MovementType.EXIT);
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO response = new InventorySummaryDTO(1L, bookSummary, 40, true);

            when(inventoryServiceImpl.registerSale(any(UpdateStockDTO.class), eq(bookId)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/sales", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantityAvailable", is(40)));

            verify(inventoryServiceImpl, times(1)).registerSale(any(UpdateStockDTO.class), eq(bookId));
        }

        @Test
        @DisplayName("Should return bad request when quantity adjustment is negative")
        void shouldReturnBadRequestWhenQuantityAdjustmentIsNegative() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(-10, MovementType.EXIT);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/sales", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).registerSale(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when movement type is null")
        void shouldReturnBadRequestWhenMovementTypeIsNull() throws Exception {
            Long bookId = 1L;
            String invalidJson = "{\"quantityAdjustment\": 10, \"movementType\": null}";

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/sales", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).registerSale(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when book id is negative")
        void shouldReturnBadRequestWhenBookIdIsNegative() throws Exception {
            UpdateStockDTO updateStock = new UpdateStockDTO(10, MovementType.EXIT);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/sales", -1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).registerSale(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when insufficient stock")
        void shouldReturnBadRequestWhenInsufficientStock() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(100, MovementType.EXIT);

            when(inventoryServiceImpl.registerSale(any(UpdateStockDTO.class), eq(bookId)))
                    .thenThrow(new InsufficientStockException("Insufficient Stock"));

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/sales", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Insufficient Stock")));

            verify(inventoryServiceImpl, times(1)).registerSale(any(UpdateStockDTO.class), eq(bookId));
        }
    }

    @Nested
    @DisplayName("Register Entry Tests")
    class RegisterEntry {

        @Test
        @DisplayName("Should register entry when request is valid")
        void shouldRegisterEntryWhenRequestIsValid() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(20, MovementType.ENTRY);
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO response = new InventorySummaryDTO(1L, bookSummary, 70, true);

            when(inventoryServiceImpl.registerEntry(any(UpdateStockDTO.class), eq(bookId)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/entries", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantityAvailable", is(70)));

            verify(inventoryServiceImpl, times(1)).registerEntry(any(UpdateStockDTO.class), eq(bookId));
        }

        @Test
        @DisplayName("Should return bad request when quantity adjustment is negative")
        void shouldReturnBadRequestWhenQuantityAdjustmentIsNegative() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(-10, MovementType.ENTRY);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/entries", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).registerEntry(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when movement type is null")
        void shouldReturnBadRequestWhenMovementTypeIsNull() throws Exception {
            Long bookId = 1L;
            String invalidJson = "{\"quantityAdjustment\": 20, \"movementType\": null}";

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/entries", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).registerEntry(any(), any());
        }
    }

    @Nested
    @DisplayName("Positive Adjustment Tests")
    class PositiveAdjustment {

        @Test
        @DisplayName("Should apply positive adjustment when request is valid")
        void shouldApplyPositiveAdjustmentWhenRequestIsValid() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(5, MovementType.POSITIVE_ADJUSTMENT);
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO response = new InventorySummaryDTO(1L, bookSummary, 55, true);

            when(inventoryServiceImpl.positiveAdjustment(any(UpdateStockDTO.class), eq(bookId)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/adjustment/positive", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantityAvailable", is(55)));

            verify(inventoryServiceImpl, times(1)).positiveAdjustment(any(UpdateStockDTO.class), eq(bookId));
        }

        @Test
        @DisplayName("Should return bad request when quantity adjustment is negative")
        void shouldReturnBadRequestWhenQuantityAdjustmentIsNegative() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(-5, MovementType.POSITIVE_ADJUSTMENT);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/adjustment/positive", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).positiveAdjustment(any(), any());
        }
    }

    @Nested
    @DisplayName("Negative Adjustment Tests")
    class NegativeAdjustment {

        @Test
        @DisplayName("Should apply negative adjustment when request is valid")
        void shouldApplyNegativeAdjustmentWhenRequestIsValid() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(5, MovementType.NEGATIVE_ADJUSTMENT);
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO response = new InventorySummaryDTO(1L, bookSummary, 45, true);

            when(inventoryServiceImpl.negativeAdjustment(any(UpdateStockDTO.class), eq(bookId)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/adjustment/negative", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantityAvailable", is(45)));

            verify(inventoryServiceImpl, times(1)).negativeAdjustment(any(UpdateStockDTO.class), eq(bookId));
        }

        @Test
        @DisplayName("Should return bad request when adjustment would result in negative stock")
        void shouldReturnBadRequestWhenAdjustmentWouldResultInNegativeStock() throws Exception {
            Long bookId = 1L;
            UpdateStockDTO updateStock = new UpdateStockDTO(100, MovementType.NEGATIVE_ADJUSTMENT);

            when(inventoryServiceImpl.negativeAdjustment(any(UpdateStockDTO.class), eq(bookId)))
                    .thenThrow(new InvalidAdjustmentException("The adjustment would leave negative stock."));

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/adjustment/negative", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStock)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("negative stock")));

            verify(inventoryServiceImpl, times(1)).negativeAdjustment(any(UpdateStockDTO.class), eq(bookId));
        }
    }

    @Nested
    @DisplayName("Create Tests")
    class Create {

        @Test
        @DisplayName("Should create inventory when request is valid")
        void shouldCreateInventoryWhenRequestIsValid() throws Exception {
            CreateInventoryDTO createDTO = new CreateInventoryDTO(1L, 50, 10, 100);
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventorySummaryDTO response = new InventorySummaryDTO(1L, bookSummary, 50, true);

            when(inventoryServiceImpl.create(any(CreateInventoryDTO.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantityAvailable", is(50)));

            verify(inventoryServiceImpl, times(1)).create(any(CreateInventoryDTO.class));
        }

        @Test
        @DisplayName("Should return conflict when inventory already exists")
        void shouldReturnConflictWhenInventoryAlreadyExists() throws Exception {
            CreateInventoryDTO createDTO = new CreateInventoryDTO(1L, 50, 10, 100);

            when(inventoryServiceImpl.create(any(CreateInventoryDTO.class)))
                    .thenThrow(new DuplicateEntityException("Inventory", "BookId", 1L));

            mockMvc.perform(post("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Inventory")));

            verify(inventoryServiceImpl, times(1)).create(any(CreateInventoryDTO.class));
        }

        @Test
        @DisplayName("Should return bad request when book id is null")
        void shouldReturnBadRequestWhenBookIdIsNull() throws Exception {
            String invalidJson = "{\"bookId\": null, \"quantityAvailable\": 50, \"stockMin\": 10, \"stockMax\": 100}";

            mockMvc.perform(post("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).create(any());
        }

        @Test
        @DisplayName("Should return bad request when quantity available is negative")
        void shouldReturnBadRequestWhenQuantityAvailableIsNegative() throws Exception {
            CreateInventoryDTO createDTO = new CreateInventoryDTO(1L, -10, 10, 100);

            mockMvc.perform(post("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).create(any());
        }

        @Test
        @DisplayName("Should return bad request when stock min is negative")
        void shouldReturnBadRequestWhenStockMinIsNegative() throws Exception {
            CreateInventoryDTO createDTO = new CreateInventoryDTO(1L, 50, -10, 100);

            mockMvc.perform(post("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).create(any());
        }

        @Test
        @DisplayName("Should return bad request when stock max is zero")
        void shouldReturnBadRequestWhenStockMaxIsZero() throws Exception {
            CreateInventoryDTO createDTO = new CreateInventoryDTO(1L, 50, 10, 0);

            mockMvc.perform(post("/api/v1/inventory")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Release Reservation Tests")
    class ReleaseReservation {

        @Test
        @DisplayName("Should release reservation when request is valid")
        void shouldReleaseReservationWhenRequestIsValid() throws Exception {
            Long bookId = 1L;
            Integer quantity = 5;

            doNothing().when(inventoryServiceImpl).releaseReservation(bookId, quantity);

            mockMvc.perform(delete("/api/v1/inventory/book/{bookId}/reservations", bookId)
                            .param("quantity", quantity.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(inventoryServiceImpl, times(1)).releaseReservation(bookId, quantity);
        }
    }

    @Nested
    @DisplayName("Reserve Stock Tests")
    class ReserveStock {

        @Test
        @DisplayName("Should reserve stock when request is valid")
        void shouldReserveStockWhenRequestIsValid() throws Exception {
            Long bookId = 1L;
            Integer quantity = 10;

            doNothing().when(inventoryServiceImpl).reserveStock(bookId, quantity);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/reservations", bookId)
                            .param("quantity", quantity.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(inventoryServiceImpl, times(1)).reserveStock(bookId, quantity);
        }

        @Test
        @DisplayName("Should return bad request when quantity is negative")
        void shouldReturnBadRequestWhenQuantityIsNegative() throws Exception {
            Long bookId = 1L;

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/reservations", bookId)
                            .param("quantity", "-10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).reserveStock(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when insufficient stock")
        void shouldReturnBadRequestWhenInsufficientStock() throws Exception {
            Long bookId = 1L;
            Integer quantity = 200;

            doThrow(new InsufficientStockException("Insufficient stock"))
                    .when(inventoryServiceImpl).reserveStock(bookId, quantity);

            mockMvc.perform(post("/api/v1/inventory/book/{bookId}/reservations", bookId)
                            .param("quantity", quantity.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Insufficient stock")));

            verify(inventoryServiceImpl, times(1)).reserveStock(bookId, quantity);
        }
    }

    @Nested
    @DisplayName("Update Thresholds Tests")
    class UpdateThresholds {

        @Test
        @DisplayName("Should update thresholds when request is valid")
        void shouldUpdateThresholdsWhenRequestIsValid() throws Exception {
            Long bookId = 1L;
            Integer stockMin = 15;
            Integer stockMax = 120;
            BookSummaryDTO bookSummary = new BookSummaryDTO(1L, "Book 1", "ISBN1", "Author 1");
            InventoryResponseDTO response = new InventoryResponseDTO(
                    1L, bookSummary, 50, 5, stockMin, stockMax, LocalDateTime.now(), true, 45
            );

            when(inventoryServiceImpl.updateThresholds(bookId, stockMin, stockMax))
                    .thenReturn(response);

            mockMvc.perform(patch("/api/v1/inventory/book/{bookId}/thresholds", bookId)
                            .param("stockMin", stockMin.toString())
                            .param("stockMax", stockMax.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.stockMin", is(15)))
                    .andExpect(jsonPath("$.stockMax", is(120)));

            verify(inventoryServiceImpl, times(1)).updateThresholds(bookId, stockMin, stockMax);
        }

        @Test
        @DisplayName("Should return bad request when stock min is greater than stock max")
        void shouldReturnBadRequestWhenStockMinIsGreaterThanStockMax() throws Exception {
            Long bookId = 1L;
            Integer stockMin = 100;
            Integer stockMax = 50;

            when(inventoryServiceImpl.updateThresholds(bookId, stockMin, stockMax))
                    .thenThrow(new InvalidStockThresholdException("Minimum stock cannot be greater than or equal to the maximum stock"));

            mockMvc.perform(patch("/api/v1/inventory/book/{bookId}/thresholds", bookId)
                            .param("stockMin", stockMin.toString())
                            .param("stockMax", stockMax.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Minimum stock")));

            verify(inventoryServiceImpl, times(1)).updateThresholds(bookId, stockMin, stockMax);
        }

        @Test
        @DisplayName("Should return bad request when stock min is negative")
        void shouldReturnBadRequestWhenStockMinIsNegative() throws Exception {
            Long bookId = 1L;

            mockMvc.perform(patch("/api/v1/inventory/book/{bookId}/thresholds", bookId)
                            .param("stockMin", "-10")
                            .param("stockMax", "100")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).updateThresholds(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Disable By Id Tests")
    class DisableById {

        @Test
        @DisplayName("Should disable inventory when id is valid")
        void shouldDisableInventoryWhenIdIsValid() throws Exception {
            Long inventoryId = 1L;

            doNothing().when(inventoryServiceImpl).disableById(inventoryId);

            mockMvc.perform(patch("/api/v1/inventory/{inventoryId}/disable", inventoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(inventoryServiceImpl, times(1)).disableById(inventoryId);
        }

        @Test
        @DisplayName("Should return not found when inventory does not exist")
        void shouldReturnNotFoundWhenInventoryDoesNotExist() throws Exception {
            Long inventoryId = 999L;

            doThrow(new ResourceNotFoundException("Inventory", "Id", inventoryId))
                    .when(inventoryServiceImpl).disableById(inventoryId);

            mockMvc.perform(patch("/api/v1/inventory/{inventoryId}/disable", inventoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Inventory")));

            verify(inventoryServiceImpl, times(1)).disableById(inventoryId);
        }

        @Test
        @DisplayName("Should return bad request when id is negative")
        void shouldReturnBadRequestWhenIdIsNegative() throws Exception {
            mockMvc.perform(patch("/api/v1/inventory/{inventoryId}/disable", -1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryServiceImpl, never()).disableById(any());
        }
    }
}
