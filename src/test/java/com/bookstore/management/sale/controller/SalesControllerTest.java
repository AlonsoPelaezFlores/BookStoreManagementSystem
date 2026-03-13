package com.bookstore.management.sale.controller;

import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.sales.controller.SalesController;
import com.bookstore.management.sales.dto.SaleRequestDTO;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.dto.SalesDetailRequestDTO;
import com.bookstore.management.sales.dto.SalesDetailResponseDTO;
import com.bookstore.management.sales.model.PaymentMethod;
import com.bookstore.management.sales.model.SalesStatus;
import com.bookstore.management.sales.service.SaleService;
import com.bookstore.management.shared.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(SalesController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class SalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService saleService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookSummaryDTO buildBook() {
        return new BookSummaryDTO(1L, "Clean Code", "978-0132350884", new BigDecimal("29.99"), "Robert C. Martin");
    }

    private CustomerSummaryDTO buildCustomer() {
        return new CustomerSummaryDTO(1L, "Alonso García", "alonso@email.com");
    }

    private SalesDetailResponseDTO buildDetail() {
        return new SalesDetailResponseDTO(
                1L,
                buildBook(),
                2,
                new BigDecimal("29.99"),
                new BigDecimal("0.00"),
                new BigDecimal("59.98")
        );
    }

    private SaleResponseDTO buildSaleResponse() {
        return new SaleResponseDTO(
                1L,
                buildCustomer(),
                SalesStatus.PENDING,
                PaymentMethod.CARD,
                List.of(buildDetail()),
                new BigDecimal("59.98"),
                "Test observation"
        );
    }

    private SaleRequestDTO buildSaleRequest() {
        SalesDetailRequestDTO detail = SalesDetailRequestDTO.builder()
                .bookId(1L)
                .quantity(2)
                .discountPercentage(new BigDecimal("0.00"))
                .build();

        return SaleRequestDTO.builder()
                .customerId(1L)
                .paymentMethod(PaymentMethod.CARD)
                .items(List.of(detail))
                .discountPercentage(new BigDecimal("0.00"))
                .observation("Test observation")
                .build();
    }

    @Nested
    class GetAllSales {

        @Test
        @DisplayName("Should return 200 with list of sales")
        void shouldReturnAllSales() throws Exception {
            when(saleService.findAll()).thenReturn(List.of(buildSaleResponse()));

            mockMvc.perform(get("/api/sales"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].status").value("PENDING"));

            verify(saleService).findAll();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no sales exist")
        void shouldReturnEmptyListWhenNoSales() throws Exception {
            when(saleService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/sales"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    class GetSaleById {

        @Test
        @DisplayName("Should return 200 with sale when ID exists")
        void shouldReturnSaleWhenIdExists() throws Exception {
            when(saleService.findById(1L)).thenReturn(buildSaleResponse());

            mockMvc.perform(get("/api/sales/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.customer.fullName").value("Alonso García"))
                    .andExpect(jsonPath("$.total").value(59.98));

            verify(saleService).findById(1L);
        }

        @Test
        @DisplayName("Should return 400 when ID is negative")
        void shouldReturn400WhenIdIsNegative() throws Exception {
            mockMvc.perform(get("/api/sales/-1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when ID is zero")
        void shouldReturn400WhenIdIsZero() throws Exception {
            mockMvc.perform(get("/api/sales/0"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetSalesByCustomer {

        @Test
        @DisplayName("Should return 200 with sales for given customer")
        void shouldReturnSalesForCustomer() throws Exception {
            when(saleService.findByCustomerId(1L)).thenReturn(List.of(buildSaleResponse()));

            mockMvc.perform(get("/api/sales/customer/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].customer.id").value(1L));

            verify(saleService).findByCustomerId(1L);
        }

        @Test
        @DisplayName("Should return 200 with empty list when customer has no sales")
        void shouldReturnEmptyListWhenCustomerHasNoSales() throws Exception {
            when(saleService.findByCustomerId(99L)).thenReturn(List.of());

            mockMvc.perform(get("/api/sales/customer/99"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return 400 when customer ID is negative")
        void shouldReturn400WhenCustomerIdIsNegative() throws Exception {
            mockMvc.perform(get("/api/sales/customer/-1"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetSalesByStatus {

        @Test
        @DisplayName("Should return 200 with PENDING sales")
        void shouldReturnPendingSales() throws Exception {
            when(saleService.findByStatus(SalesStatus.PENDING)).thenReturn(List.of(buildSaleResponse()));

            mockMvc.perform(get("/api/sales/status/PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value("PENDING"));

            verify(saleService).findByStatus(SalesStatus.PENDING);
        }

        @Test
        @DisplayName("Should return 400 when status is invalid")
        void shouldReturn400WhenStatusIsInvalid() throws Exception {
            mockMvc.perform(get("/api/sales/status/INVALID_STATUS"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetSalesByDateRange {

        @Test
        @DisplayName("Should return 200 with sales within date range")
        void shouldReturnSalesWithinDateRange() throws Exception {
            LocalDate start = LocalDate.of(2024, 1, 1);
            LocalDate end = LocalDate.of(2024, 12, 31);

            when(saleService.findByDateRange(start, end)).thenReturn(List.of(buildSaleResponse()));

            mockMvc.perform(get("/api/sales/date-range")
                            .param("start", "2024-01-01")
                            .param("end", "2024-12-31"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(saleService).findByDateRange(start, end);
        }

        @Test
        @DisplayName("Should return 400 when start param is missing")
        void shouldReturn400WhenStartParamIsMissing() throws Exception {
            mockMvc.perform(get("/api/sales/date-range")
                            .param("end", "2024-12-31"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when end param is missing")
        void shouldReturn400WhenEndParamIsMissing() throws Exception {
            mockMvc.perform(get("/api/sales/date-range")
                            .param("start", "2024-01-01"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when date format is invalid")
        void shouldReturn400WhenDateFormatIsInvalid() throws Exception {
            mockMvc.perform(get("/api/sales/date-range")
                            .param("start", "01-01-2024")
                            .param("end", "31-12-2024"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 200 with empty list when no sales in range")
        void shouldReturnEmptyListWhenNoSalesInDateRange() throws Exception {
            LocalDate start = LocalDate.of(2024, 1, 1);
            LocalDate end = LocalDate.of(2024, 1, 31);

            when(saleService.findByDateRange(start, end)).thenReturn(List.of());

            mockMvc.perform(get("/api/sales/date-range")
                            .param("start", "2024-01-01")
                            .param("end", "2024-01-31"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    class CreateSale {

        @Test
        @DisplayName("Should return 201 with created sale")
        void shouldReturn201WhenSaleIsCreated() throws Exception {
            SaleRequestDTO request = buildSaleRequest();
            SaleResponseDTO response = buildSaleResponse();

            when(saleService.createSale(any(SaleRequestDTO.class))).thenReturn(response);

            mockMvc.perform(post("/api/sales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.status").value("PENDING"));

            verify(saleService).createSale(any(SaleRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 400 when items list is empty")
        void shouldReturn400WhenItemsIsEmpty() throws Exception {
            SaleRequestDTO request = SaleRequestDTO.builder()
                    .customerId(1L)
                    .paymentMethod(PaymentMethod.CASH)
                    .items(List.of())
                    .build();

            mockMvc.perform(post("/api/sales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when paymentMethod is null")
        void shouldReturn400WhenPaymentMethodIsNull() throws Exception {
            SaleRequestDTO request = SaleRequestDTO.builder()
                    .customerId(1L)
                    .paymentMethod(null)
                    .items(List.of(SalesDetailRequestDTO.builder()
                            .bookId(1L)
                            .quantity(1)
                            .build()))
                    .build();

            mockMvc.perform(post("/api/sales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when request body is missing")
        void shouldReturn400WhenBodyIsMissing() throws Exception {
            mockMvc.perform(post("/api/sales")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class CompleteSale {

        @Test
        @DisplayName("Should return 200 with completed sale")
        void shouldReturn200WhenSaleIsCompleted() throws Exception {
            SaleResponseDTO completed = new SaleResponseDTO(
                    1L, buildCustomer(), SalesStatus.COMPLETED,
                    PaymentMethod.CARD, List.of(buildDetail()),
                    new BigDecimal("59.98"), "Test observation"
            );

            when(saleService.completeSale(1L)).thenReturn(completed);

            mockMvc.perform(patch("/api/sales/1/complete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));

            verify(saleService).completeSale(1L);
        }

        @Test
        @DisplayName("Should return 400 when ID is negative")
        void shouldReturn400WhenIdIsNegative() throws Exception {
            mockMvc.perform(patch("/api/sales/-1/complete"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class CancelSale {

        @Test
        @DisplayName("Should return 200 with cancelled sale")
        void shouldReturn200WhenSaleIsCancelled() throws Exception {
            SaleResponseDTO cancelled = new SaleResponseDTO(
                    1L, buildCustomer(), SalesStatus.CANCELLED,
                    PaymentMethod.CARD, List.of(buildDetail()),
                    new BigDecimal("59.98"), "Test observation"
            );

            when(saleService.cancelSale(1L)).thenReturn(cancelled);

            mockMvc.perform(patch("/api/sales/1/cancel"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELLED"));

            verify(saleService).cancelSale(1L);
        }

        @Test
        @DisplayName("Should return 400 when ID is negative")
        void shouldReturn400WhenIdIsNegative() throws Exception {
            mockMvc.perform(patch("/api/sales/-1/cancel"))
                    .andExpect(status().isBadRequest());
        }
    }
}
