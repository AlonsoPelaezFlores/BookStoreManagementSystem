package com.bookstore.management.inventory.controller;

import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.dto.InventorySummaryDTO;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.service.InventoryMovementServiceImpl;
import com.bookstore.management.shared.exception.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InventoryMovementController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class InventoryMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryMovementServiceImpl inventoryMovementServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Get Inventory Movements Tests")
    class Get_Inventory_Movements {

        @Test
        @DisplayName("Should return paginated movements when inventory id is valid")
        void shouldReturnPaginatedMovementsWhenInventoryIdIsValid() throws Exception {
            Long inventoryId = 1L;
            InventorySummaryDTO inventorySummary = new InventorySummaryDTO(1L, null, 50, true);

            InventoryMovementResponseDTO movement1 = new InventoryMovementResponseDTO(
                    1L, inventorySummary, 10, 40, 50, MovementType.ENTRY,
                    "Entry", "SYSTEM", LocalDateTime.now()
            );

            InventoryMovementResponseDTO movement2 = new InventoryMovementResponseDTO(
                    2L, inventorySummary, -5, 50, 45, MovementType.EXIT,
                    "Exit", "SYSTEM", LocalDateTime.now()
            );

            List<InventoryMovementResponseDTO> movements = Arrays.asList(movement1, movement2);
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(movements, PageRequest.of(0, 10), 2);

            when(inventoryMovementServiceImpl.findAllByInventoryId(eq(inventoryId), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-inventory/{inventoryId}", inventoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(1)))
                    .andExpect(jsonPath("$.content[0].movementType", is("ENTRY")))
                    .andExpect(jsonPath("$.content[0].affectedQuantity", is(10)))
                    .andExpect(jsonPath("$.content[1].id", is(2)))
                    .andExpect(jsonPath("$.content[1].movementType", is("EXIT")))
                    .andExpect(jsonPath("$.content[1].affectedQuantity", is(-5)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.size", is(10)));

            verify(inventoryMovementServiceImpl, times(1)).findAllByInventoryId(eq(inventoryId), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when inventory has no movements")
        void shouldReturnEmptyPageWhenInventoryHasNoMovements() throws Exception {
            Long inventoryId = 1L;
            Page<InventoryMovementResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findAllByInventoryId(eq(inventoryId), any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/v1/movements/by-inventory/{inventoryId}", inventoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));

            verify(inventoryMovementServiceImpl, times(1)).findAllByInventoryId(eq(inventoryId), any(Pageable.class));
        }

        @Test
        @DisplayName("Should use default pagination when not specified")
        void shouldUseDefaultPaginationWhenNotSpecified() throws Exception {
            Long inventoryId = 1L;
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findAllByInventoryId(eq(inventoryId), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-inventory/{inventoryId}", inventoryId))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl).findAllByInventoryId(
                    eq(inventoryId),
                    argThat(pageable ->
                            pageable.getPageSize() == 10 &&
                                    pageable.getPageNumber() == 0 &&
                                    pageable.getSort().isSorted()
                    )
            );
        }

        @Test
        @DisplayName("Should accept custom pagination parameters")
        void shouldAcceptCustomPaginationParameters() throws Exception {
            Long inventoryId = 1L;
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findAllByInventoryId(eq(inventoryId), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-inventory/{inventoryId}", inventoryId)
                            .param("page", "2")
                            .param("size", "20"))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl).findAllByInventoryId(
                    eq(inventoryId),
                    argThat(pageable ->
                            pageable.getPageSize() == 20 &&
                                    pageable.getPageNumber() == 2
                    )
            );
        }

        @Test
        @DisplayName("Should return bad request when inventory id is zero")
        void shouldReturnBadRequestWhenInventoryIdIsZero() throws Exception {
            mockMvc.perform(get("/api/v1/movements/by-inventory/{inventoryId}", 0)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryMovementServiceImpl, never()).findAllByInventoryId(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when inventory id is negative")
        void shouldReturnBadRequestWhenInventoryIdIsNegative() throws Exception {
            mockMvc.perform(get("/api/v1/movements/by-inventory/{inventoryId}", -1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryMovementServiceImpl, never()).findAllByInventoryId(any(), any());
        }
    }

    @Nested
    @DisplayName("Get Movements By Type Tests")
    class Get_Movements_By_Type {

        @Test
        @DisplayName("Should return movements when movement type is valid")
        void shouldReturnMovementsWhenMovementTypeIsValid() throws Exception {
            MovementType movementType = MovementType.ENTRY;
            InventorySummaryDTO inventorySummary = new InventorySummaryDTO(1L, null, 50, true);

            InventoryMovementResponseDTO movement = new InventoryMovementResponseDTO(
                    1L, inventorySummary, 10, 40, 50, MovementType.ENTRY,
                    "Entry", "SYSTEM", LocalDateTime.now()
            );

            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.singletonList(movement));

            when(inventoryMovementServiceImpl.findByMovementType(eq(movementType), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-type")
                            .param("type", "ENTRY")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].movementType", is("ENTRY")))
                    .andExpect(jsonPath("$.content[0].affectedQuantity", is(10)));

            verify(inventoryMovementServiceImpl, times(1)).findByMovementType(eq(movementType), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no movements match type")
        void shouldReturnEmptyPageWhenNoMovementsMatchType() throws Exception {
            MovementType movementType = MovementType.RESERVE;
            Page<InventoryMovementResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findByMovementType(eq(movementType), any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/v1/movements/by-type")
                            .param("type", "RESERVE")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));

            verify(inventoryMovementServiceImpl, times(1)).findByMovementType(eq(movementType), any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle all movement types correctly")
        void shouldHandleAllMovementTypesCorrectly() throws Exception {
            MovementType movementType = MovementType.POSITIVE_ADJUSTMENT;
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findByMovementType(eq(movementType), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-type")
                            .param("type", "POSITIVE_ADJUSTMENT")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl, times(1)).findByMovementType(eq(movementType), any(Pageable.class));
        }

        @Test
        @DisplayName("Should use default pagination for movement type query")
        void shouldUseDefaultPaginationForMovementTypeQuery() throws Exception {
            MovementType movementType = MovementType.EXIT;
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findByMovementType(eq(movementType), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-type")
                            .param("type", "EXIT"))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl).findByMovementType(
                    eq(movementType),
                    argThat(pageable ->
                            pageable.getPageSize() == 10 &&
                                    pageable.getSort().isSorted()
                    )
            );
        }

        @Test
        @DisplayName("Should return bad request when movement type is invalid")
        void shouldReturnBadRequestWhenMovementTypeIsInvalid() throws Exception {
            mockMvc.perform(get("/api/v1/movements/by-type")
                            .param("type", "INVALID_TYPE")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryMovementServiceImpl, never()).findByMovementType(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when type parameter is missing")
        void shouldReturnBadRequestWhenTypeParameterIsMissing() throws Exception {
            mockMvc.perform(get("/api/v1/movements/by-type")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryMovementServiceImpl, never()).findByMovementType(any(), any());
        }
    }

    @Nested
    @DisplayName("Get Movements Between Dates Tests")
    class Get_Movements_Between_Dates {

        @Test
        @DisplayName("Should return movements within date range")
        void shouldReturnMovementsWithinDateRange() throws Exception {
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);
            InventorySummaryDTO inventorySummary = new InventorySummaryDTO(1L, null, 50, true);

            InventoryMovementResponseDTO movement = new InventoryMovementResponseDTO(
                    1L, inventorySummary, 10, 40, 50, MovementType.ENTRY,
                    "Entry", "SYSTEM", LocalDateTime.now()
            );

            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.singletonList(movement));

            when(inventoryMovementServiceImpl.findByDateRange(eq(startDate), eq(endDate), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("startDate", "2024-01-01")
                            .param("endDate", "2024-12-31")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id", is(1)));

            verify(inventoryMovementServiceImpl, times(1)).findByDateRange(eq(startDate), eq(endDate), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no movements in date range")
        void shouldReturnEmptyPageWhenNoMovementsInDateRange() throws Exception {
            LocalDate startDate = LocalDate.of(2020, 1, 1);
            LocalDate endDate = LocalDate.of(2020, 12, 31);
            Page<InventoryMovementResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findByDateRange(eq(startDate), eq(endDate), any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("startDate", "2020-01-01")
                            .param("endDate", "2020-12-31")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));

            verify(inventoryMovementServiceImpl, times(1)).findByDateRange(eq(startDate), eq(endDate), any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle same day date range")
        void shouldHandleSameDayDateRange() throws Exception {
            LocalDate sameDate = LocalDate.of(2024, 6, 15);
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findByDateRange(eq(sameDate), eq(sameDate), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("startDate", "2024-06-15")
                            .param("endDate", "2024-06-15")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl, times(1)).findByDateRange(eq(sameDate), eq(sameDate), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return bad request when start date is after end date")
        void shouldReturnBadRequestWhenStartDateIsAfterEndDate() throws Exception {
            LocalDate startDate = LocalDate.of(2024, 12, 31);
            LocalDate endDate = LocalDate.of(2024, 1, 1);

            when(inventoryMovementServiceImpl.findByDateRange(eq(startDate), eq(endDate), any(Pageable.class)))
                    .thenThrow(new IllegalArgumentException("Start date must be before or equal to end date"));

            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("startDate", "2024-12-31")
                            .param("endDate", "2024-01-01")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Start date must be before or equal to end date")));

            verify(inventoryMovementServiceImpl, times(1)).findByDateRange(eq(startDate), eq(endDate), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return bad request when start date is missing")
        void shouldReturnBadRequestWhenStartDateIsMissing() throws Exception {
            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("endDate", "2024-12-31")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryMovementServiceImpl, never()).findByDateRange(any(), any(), any());
        }

        @Test
        @DisplayName("Should return bad request when end date is missing")
        void shouldReturnBadRequestWhenEndDateIsMissing() throws Exception {
            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("startDate", "2024-01-01")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryMovementServiceImpl, never()).findByDateRange(any(), any(), any());
        }

        @Test
        @DisplayName("Should return bad request when date format is invalid")
        void shouldReturnBadRequestWhenDateFormatIsInvalid() throws Exception {
            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("startDate", "invalid-date")
                            .param("endDate", "2024-12-31")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(inventoryMovementServiceImpl, never()).findByDateRange(any(), any(), any());
        }

        @Test
        @DisplayName("Should use default pagination for date range query")
        void shouldUseDefaultPaginationForDateRangeQuery() throws Exception {
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findByDateRange(eq(startDate), eq(endDate), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/by-date-range")
                            .param("startDate", "2024-01-01")
                            .param("endDate", "2024-12-31"))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl).findByDateRange(
                    eq(startDate),
                    eq(endDate),
                    argThat(pageable ->
                            pageable.getPageSize() == 10 &&
                                    pageable.getSort().isSorted()
                    )
            );
        }
    }

    @Nested
    @DisplayName("Get Recent Movements Tests")
    class Get_Recent_Movements {

        @Test
        @DisplayName("Should return recent movements with default pagination")
        void shouldReturnRecentMovementsWithDefaultPagination() throws Exception {
            InventorySummaryDTO inventorySummary = new InventorySummaryDTO(1L, null, 50, true);

            InventoryMovementResponseDTO movement1 = new InventoryMovementResponseDTO(
                    1L, inventorySummary, 10, 40, 50, MovementType.ENTRY,
                    "Entry", "SYSTEM", LocalDateTime.now()
            );

            InventoryMovementResponseDTO movement2 = new InventoryMovementResponseDTO(
                    2L, inventorySummary, -5, 50, 45, MovementType.EXIT,
                    "Exit", "SYSTEM", LocalDateTime.now()
            );

            List<InventoryMovementResponseDTO> movements = Arrays.asList(movement1, movement2);
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(movements);

            when(inventoryMovementServiceImpl.findRecentMovements(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/recent")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(1)))
                    .andExpect(jsonPath("$.content[1].id", is(2)));

            verify(inventoryMovementServiceImpl, times(1)).findRecentMovements(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no recent movements exist")
        void shouldReturnEmptyPageWhenNoRecentMovementsExist() throws Exception {
            Page<InventoryMovementResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findRecentMovements(any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/v1/movements/recent")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));

            verify(inventoryMovementServiceImpl, times(1)).findRecentMovements(any(Pageable.class));
        }

        @Test
        @DisplayName("Should use default pagination parameters for recent movements")
        void shouldUseDefaultPaginationParametersForRecentMovements() throws Exception {
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findRecentMovements(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/recent"))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl).findRecentMovements(
                    argThat(pageable ->
                            pageable.getPageSize() == 10 &&
                                    pageable.getPageNumber() == 0 &&
                                    pageable.getSort().isSorted()
                    )
            );
        }

        @Test
        @DisplayName("Should accept custom pagination for recent movements")
        void shouldAcceptCustomPaginationForRecentMovements() throws Exception {
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(Collections.emptyList());

            when(inventoryMovementServiceImpl.findRecentMovements(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/recent")
                            .param("page", "1")
                            .param("size", "25"))
                    .andExpect(status().isOk());

            verify(inventoryMovementServiceImpl).findRecentMovements(
                    argThat(pageable ->
                            pageable.getPageSize() == 25 &&
                                    pageable.getPageNumber() == 1
                    )
            );
        }

        @Test
        @DisplayName("Should return movements in descending order by creation date")
        void shouldReturnMovementsInDescendingOrderByCreationDate() throws Exception {
            InventorySummaryDTO inventorySummary = new InventorySummaryDTO(1L, null, 50, true);

            InventoryMovementResponseDTO newerMovement = new InventoryMovementResponseDTO(
                    2L, inventorySummary, -5, 50, 45, MovementType.EXIT,
                    "Exit", "SYSTEM", LocalDateTime.now()
            );

            InventoryMovementResponseDTO olderMovement = new InventoryMovementResponseDTO(
                    1L, inventorySummary, 10, 40, 50, MovementType.ENTRY,
                    "Entry", "SYSTEM", LocalDateTime.now().minusDays(1)
            );

            List<InventoryMovementResponseDTO> movements = Arrays.asList(newerMovement, olderMovement);
            Page<InventoryMovementResponseDTO> page = new PageImpl<>(movements);

            when(inventoryMovementServiceImpl.findRecentMovements(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/movements/recent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id", is(2)))
                    .andExpect(jsonPath("$.content[1].id", is(1)));

            verify(inventoryMovementServiceImpl, times(1)).findRecentMovements(any(Pageable.class));
        }
    }
}
