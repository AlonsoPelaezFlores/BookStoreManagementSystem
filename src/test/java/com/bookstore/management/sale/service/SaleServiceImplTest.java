package com.bookstore.management.sale.service;

import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.book.model.Gender;
import com.bookstore.management.book.repository.BookRepository;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.customer.repository.CustomerRepository;
import com.bookstore.management.inventory.dto.UpdateStockDTO;
import com.bookstore.management.inventory.service.InventoryServiceImpl;
import com.bookstore.management.sales.dto.SaleRequestDTO;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.dto.SalesDetailRequestDTO;
import com.bookstore.management.sales.dto.SalesDetailResponseDTO;
import com.bookstore.management.sales.mapper.SaleMapper;
import com.bookstore.management.sales.model.PaymentMethod;
import com.bookstore.management.sales.model.Sale;
import com.bookstore.management.sales.model.SalesDetail;
import com.bookstore.management.sales.model.SalesStatus;
import com.bookstore.management.sales.repository.SaleRepository;
import com.bookstore.management.sales.service.SaleServiceImpl;
import com.bookstore.management.shared.exception.custom.InsufficientReservedStockException;
import com.bookstore.management.shared.exception.custom.InsufficientStockException;
import com.bookstore.management.shared.exception.custom.InvalidSalesStatusException;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceImplTest {


    @Mock
    private SaleRepository saleRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryServiceImpl inventoryService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SaleMapper saleMapper;

    @InjectMocks
    private SaleServiceImpl  saleService;

    private Sale sale;
    private SaleResponseDTO saleResponseDTO;
    private Customer customer;
    private Book book;
    private Author author;
    private SalesDetail salesDetail;
    private SaleRequestDTO saleRequestDTO;
    private SalesDetailRequestDTO salesDetailRequestDTO;

    @BeforeEach
    void setup() {
        author = Author.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .nationality("Colombian")
                .birthDate(LocalDate.of(1927, 3, 6))
                .gender(Gender.MALE)
                .build();

        book = Book.builder()
                .id(1L)
                .isbn("978-0307474728")
                .title("One Hundred Years of Solitude")
                .publishDate(LocalDate.of(1967, 5, 30))
                .description("A landmark novel")
                .pages(417)
                .genre("Magical Realism")
                .price(new BigDecimal("29.99"))
                .discountPercent(BigDecimal.ZERO)
                .author(author)
                .build();

        customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 15))
                .build();

        salesDetail = SalesDetail.builder()
                .id(1L)
                .book(book)
                .quantity(2)
                .unitPrice(new BigDecimal("29.99"))
                .discountPercent(new BigDecimal("10.00"))
                .lineTotal(new BigDecimal("53.98"))
                .build();

        List<SalesDetail> details = new ArrayList<>();
        details.add(salesDetail);

        sale = Sale.builder()
                .id(1L)
                .customer(customer)
                .status(SalesStatus.COMPLETED)
                .paymentMethod(PaymentMethod.CASH)
                .total(new BigDecimal("53.98"))
                .details(details)
                .observation("Test sale")
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .build();

        salesDetail.setSale(sale);

        saleResponseDTO = new SaleResponseDTO(
                1L,
                new CustomerSummaryDTO(1L, "John","Doe", "john.doe@example.com"),
                SalesStatus.COMPLETED,
                PaymentMethod.CASH,
                List.of(new SalesDetailResponseDTO(
                        1L,
                        new BookSummaryDTO(1L, "One Hundred Years of Solitude", "978-0307474728", new BigDecimal("29.99"), "Gabriel García Márquez"),
                        2,
                        new BigDecimal("29.99"),
                        new BigDecimal("10.00"),
                        new BigDecimal("53.98")
                )),
                new BigDecimal("53.98"),
                "Test sale"
        );
        salesDetailRequestDTO = SalesDetailRequestDTO.builder()
                .bookId(1L)
                .quantity(2)
                .discountPercentage(new BigDecimal("10.00"))
                .build();

        saleRequestDTO = SaleRequestDTO.builder()
                .customerId(1L)
                .paymentMethod(PaymentMethod.CASH)
                .items(List.of(salesDetailRequestDTO))
                .discountPercentage(BigDecimal.ZERO)
                .observation("Test sale")
                .build();
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return sale response when sale exists")
        void shouldReturnSaleResponseWhenSaleExists() {

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            when(saleMapper.toResponseDto(sale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.findById(1L);

            assertNotNull(result);
            assertEquals(saleResponseDTO, result);
            verify(saleRepository, times(1)).findById(1L);
            verify(saleMapper, times(1)).toResponseDto(sale);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when sale does not exist")
        void shouldThrowResourceNotFoundExceptionWhenSaleDoesNotExist() {
            Long nonExistentId = 999L;
            when(saleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> saleService.findById(nonExistentId)
            );

            assertTrue(exception.getMessage().contains("Sale"));
            assertTrue(exception.getMessage().contains("Id"));
            assertTrue(exception.getMessage().contains("999"));
            verify(saleRepository, times(1)).findById(nonExistentId);
            verify(saleMapper, never()).toResponseDto(any());
        }
    }
    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return list of sale responses when sales exist")
        void shouldReturnListOfSaleResponsesWhenSalesExist() {
            List<Sale> saleList = List.of(sale);
            List<SaleResponseDTO> expectedList = List.of(saleResponseDTO);

            when(saleRepository.findAll()).thenReturn(saleList);
            when(saleMapper.toResponseDtoList(saleList)).thenReturn(expectedList);

            List<SaleResponseDTO> result = saleService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedList, result);
            verify(saleRepository, times(1)).findAll();
            verify(saleMapper, times(1)).toResponseDtoList(saleList);
        }

        @Test
        @DisplayName("should return empty list when no sales exist")
        void shouldReturnEmptyListWhenNoSalesExist() {
            List<Sale> emptyList = List.of();

            when(saleRepository.findAll()).thenReturn(emptyList);
            when(saleMapper.toResponseDtoList(emptyList)).thenReturn(List.of());

            List<SaleResponseDTO> result = saleService.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(saleRepository, times(1)).findAll();
            verify(saleMapper, times(1)).toResponseDtoList(emptyList);
        }
    }
    @Nested
    @DisplayName("findByCustomerId")
    class FindByCustomerId {

        @Test
        @DisplayName("should return list of sale responses when customer exists and has sales")
        void shouldReturnListOfSaleResponsesWhenCustomerExistsAndHasSales() {
            Long customerId = 1L;
            List<Sale> saleList = List.of(sale);
            List<SaleResponseDTO> expectedList = List.of(saleResponseDTO);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(saleRepository.findByCustomerId(customerId)).thenReturn(saleList);
            when(saleMapper.toResponseDtoList(saleList)).thenReturn(expectedList);

            List<SaleResponseDTO> result = saleService.findByCustomerId(customerId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedList, result);
            verify(customerRepository, times(1)).findById(customerId);
            verify(saleRepository, times(1)).findByCustomerId(customerId);
            verify(saleMapper, times(1)).toResponseDtoList(saleList);
        }

        @Test
        @DisplayName("should return empty list when customer exists but has no sales")
        void shouldReturnEmptyListWhenCustomerExistsButHasNoSales() {
            Long customerId = 1L;
            List<Sale> emptyList = List.of();

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(saleRepository.findByCustomerId(customerId)).thenReturn(emptyList);
            when(saleMapper.toResponseDtoList(emptyList)).thenReturn(List.of());

            List<SaleResponseDTO> result = saleService.findByCustomerId(customerId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(customerRepository, times(1)).findById(customerId);
            verify(saleRepository, times(1)).findByCustomerId(customerId);
            verify(saleMapper, times(1)).toResponseDtoList(emptyList);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when customer does not exist")
        void shouldThrowResourceNotFoundExceptionWhenCustomerDoesNotExist() {
            Long nonExistentCustomerId = 999L;

            when(customerRepository.findById(nonExistentCustomerId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> saleService.findByCustomerId(nonExistentCustomerId)
            );

            assertTrue(exception.getMessage().contains("Customer"));
            assertTrue(exception.getMessage().contains("Id"));
            assertTrue(exception.getMessage().contains("999"));
            verify(customerRepository, times(1)).findById(nonExistentCustomerId);
            verify(saleRepository, never()).findByCustomerId(any());
            verify(saleMapper, never()).toResponseDtoList(any());
        }
    }
    @Nested
    @DisplayName("findByStatus")
    class FindByStatus {

        @Test
        @DisplayName("should return list of sale responses when sales with status exist")
        void shouldReturnListOfSaleResponsesWhenSalesWithStatusExist() {
            SalesStatus status = SalesStatus.COMPLETED;
            List<Sale> saleList = List.of(sale);
            List<SaleResponseDTO> expectedList = List.of(saleResponseDTO);

            when(saleRepository.findByStatus(status)).thenReturn(saleList);
            when(saleMapper.toResponseDtoList(saleList)).thenReturn(expectedList);

            List<SaleResponseDTO> result = saleService.findByStatus(status);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedList, result);
            verify(saleRepository, times(1)).findByStatus(status);
            verify(saleMapper, times(1)).toResponseDtoList(saleList);
        }

        @Test
        @DisplayName("should return empty list when no sales with status exist")
        void shouldReturnEmptyListWhenNoSalesWithStatusExist() {
            SalesStatus status = SalesStatus.PENDING;
            List<Sale> emptyList = List.of();

            when(saleRepository.findByStatus(status)).thenReturn(emptyList);
            when(saleMapper.toResponseDtoList(emptyList)).thenReturn(List.of());

            List<SaleResponseDTO> result = saleService.findByStatus(status);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(saleRepository, times(1)).findByStatus(status);
            verify(saleMapper, times(1)).toResponseDtoList(emptyList);
        }
    }
    @Nested
    @DisplayName("findByDateRange")
    class FindByDateRange {

        @Test
        @DisplayName("should return list of sale responses when sales in date range exist")
        void shouldReturnListOfSaleResponsesWhenSalesInDateRangeExist() {
            LocalDate start = LocalDate.of(2024, 1, 1);
            LocalDate end = LocalDate.of(2024, 12, 31);
            List<Sale> saleList = List.of(sale);
            List<SaleResponseDTO> expectedList = List.of(saleResponseDTO);

            when(saleRepository.findByDateRange(start, end)).thenReturn(saleList);
            when(saleMapper.toResponseDtoList(saleList)).thenReturn(expectedList);

            List<SaleResponseDTO> result = saleService.findByDateRange(start, end);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedList, result);
            verify(saleRepository, times(1)).findByDateRange(start, end);
            verify(saleMapper, times(1)).toResponseDtoList(saleList);
        }

        @Test
        @DisplayName("should return empty list when no sales in date range exist")
        void shouldReturnEmptyListWhenNoSalesInDateRangeExist() {
            LocalDate start = LocalDate.of(2025, 1, 1);
            LocalDate end = LocalDate.of(2025, 1, 31);
            List<Sale> emptyList = List.of();

            when(saleRepository.findByDateRange(start, end)).thenReturn(emptyList);
            when(saleMapper.toResponseDtoList(emptyList)).thenReturn(List.of());

            List<SaleResponseDTO> result = saleService.findByDateRange(start, end);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(saleRepository, times(1)).findByDateRange(start, end);
            verify(saleMapper, times(1)).toResponseDtoList(emptyList);
        }
    }
    @Nested
    @DisplayName("createSale")
    class CreateSale {

        @Test
        @DisplayName("should create sale successfully when customer exists")
        void shouldCreateSaleSuccessfullyWhenCustomerExists() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);
            when(saleMapper.toResponseDto(sale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.createSale(saleRequestDTO);

            assertNotNull(result);
            assertEquals(saleResponseDTO, result);
            verify(customerRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).reserveStock(1L, 2);
            verify(saleRepository, times(1)).save(any(Sale.class));
            verify(saleMapper, times(1)).toResponseDto(sale);
        }

        @Test
        @DisplayName("should create sale successfully when customer is null")
        void shouldCreateSaleSuccessfullyWhenCustomerIsNull() {
            SaleRequestDTO requestWithoutCustomer = SaleRequestDTO.builder()
                    .customerId(null)
                    .paymentMethod(PaymentMethod.CASH)
                    .items(List.of(salesDetailRequestDTO))
                    .discountPercentage(BigDecimal.ZERO)
                    .observation("Test sale")
                    .build();

            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);
            when(saleMapper.toResponseDto(sale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.createSale(requestWithoutCustomer);

            assertNotNull(result);
            assertEquals(saleResponseDTO, result);
            verify(customerRepository, never()).findById(any());
            verify(bookRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).reserveStock(1L, 2);
            verify(saleRepository, times(1)).save(any(Sale.class));
            verify(saleMapper, times(1)).toResponseDto(sale);
        }

        @Test
        @DisplayName("should create sale with multiple items successfully")
        void shouldCreateSaleWithMultipleItemsSuccessfully() {
            Book secondBook = Book.builder()
                    .id(2L)
                    .isbn("978-0451524935")
                    .title("1984")
                    .publishDate(LocalDate.of(1949, 6, 8))
                    .pages(328)
                    .price(new BigDecimal("15.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .author(author)
                    .build();

            SalesDetailRequestDTO secondDetailRequest = SalesDetailRequestDTO.builder()
                    .bookId(2L)
                    .quantity(1)
                    .discountPercentage(BigDecimal.ZERO)
                    .build();

            SaleRequestDTO multipleItemsRequest = SaleRequestDTO.builder()
                    .customerId(1L)
                    .paymentMethod(PaymentMethod.CASH)
                    .items(List.of(salesDetailRequestDTO, secondDetailRequest))
                    .discountPercentage(BigDecimal.ZERO)
                    .observation("Test sale")
                    .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(bookRepository.findById(2L)).thenReturn(Optional.of(secondBook));
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);
            when(saleMapper.toResponseDto(sale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.createSale(multipleItemsRequest);

            assertNotNull(result);
            assertEquals(saleResponseDTO, result);
            verify(customerRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).findById(2L);
            verify(inventoryService, times(1)).reserveStock(1L, 2);
            verify(inventoryService, times(1)).reserveStock(2L, 1);
            verify(saleRepository, times(1)).save(any(Sale.class));
            verify(saleMapper, times(1)).toResponseDto(sale);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when customer does not exist")
        void shouldThrowResourceNotFoundExceptionWhenCustomerDoesNotExist() {
            Long nonExistentCustomerId = 999L;
            SaleRequestDTO requestWithInvalidCustomer = SaleRequestDTO.builder()
                    .customerId(nonExistentCustomerId)
                    .paymentMethod(PaymentMethod.CASH)
                    .items(List.of(salesDetailRequestDTO))
                    .discountPercentage(BigDecimal.ZERO)
                    .observation("Test sale")
                    .build();

            when(customerRepository.findById(nonExistentCustomerId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> saleService.createSale(requestWithInvalidCustomer)
            );

            assertTrue(exception.getMessage().contains("Customer"));
            assertTrue(exception.getMessage().contains("Id"));
            assertTrue(exception.getMessage().contains("999"));
            verify(customerRepository, times(1)).findById(nonExistentCustomerId);
            verify(bookRepository, never()).findById(any());
            verify(inventoryService, never()).reserveStock(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book does not exist")
        void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExist() {
            Long nonExistentBookId = 999L;
            SalesDetailRequestDTO invalidDetailRequest = SalesDetailRequestDTO.builder()
                    .bookId(nonExistentBookId)
                    .quantity(1)
                    .discountPercentage(BigDecimal.ZERO)
                    .build();

            SaleRequestDTO requestWithInvalidBook = SaleRequestDTO.builder()
                    .customerId(1L)
                    .paymentMethod(PaymentMethod.CASH)
                    .items(List.of(invalidDetailRequest))
                    .discountPercentage(BigDecimal.ZERO)
                    .observation("Test sale")
                    .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> saleService.createSale(requestWithInvalidBook)
            );

            assertTrue(exception.getMessage().contains("Book"));
            assertTrue(exception.getMessage().contains("Id"));
            assertTrue(exception.getMessage().contains("999"));
            verify(customerRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).findById(nonExistentBookId);
            verify(inventoryService, never()).reserveStock(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw InsufficientStockException when stock is insufficient")
        void shouldThrowInsufficientStockExceptionWhenStockIsInsufficient() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            doThrow(new InsufficientStockException("Insufficient stock"))
                    .when(inventoryService).reserveStock(1L, 2);

            InsufficientStockException exception = assertThrows(
                    InsufficientStockException.class,
                    () -> saleService.createSale(saleRequestDTO)
            );

            assertEquals("Insufficient stock", exception.getMessage());
            verify(customerRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).reserveStock(1L, 2);
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }
    }
    @Nested
    @DisplayName("completeSale")
    class CompleteSale {

        @Test
        @DisplayName("should complete sale successfully when status is pending")
        void shouldCompleteSaleSuccessfullyWhenStatusIsPending() {
            sale.setStatus(SalesStatus.PENDING);
            Sale completedSale = Sale.builder()
                    .id(1L)
                    .customer(customer)
                    .status(SalesStatus.COMPLETED)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("53.98"))
                    .details(sale.getDetails())
                    .observation("Test sale")
                    .createdAt(LocalDateTime.now())
                    .createdBy(1L)
                    .build();

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            when(saleRepository.save(any(Sale.class))).thenReturn(completedSale);
            when(saleMapper.toResponseDto(completedSale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.completeSale(1L);

            assertNotNull(result);
            assertEquals(saleResponseDTO, result);
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).registerSale(any(UpdateStockDTO.class), eq(1L));
            verify(saleRepository, times(1)).save(any(Sale.class));
            verify(saleMapper, times(1)).toResponseDto(completedSale);
        }

        @Test
        @DisplayName("should register sales for all items when completing sale")
        void shouldRegisterSalesForAllItemsWhenCompletingSale() {
            Book secondBook = Book.builder()
                    .id(2L)
                    .isbn("978-0451524935")
                    .title("1984")
                    .publishDate(LocalDate.of(1949, 6, 8))
                    .pages(328)
                    .price(new BigDecimal("15.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .author(author)
                    .build();

            SalesDetail secondDetail = SalesDetail.builder()
                    .id(2L)
                    .sale(sale)
                    .book(secondBook)
                    .quantity(3)
                    .unitPrice(new BigDecimal("15.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .lineTotal(new BigDecimal("47.97"))
                    .build();

            sale.getDetails().add(secondDetail);
            sale.setStatus(SalesStatus.PENDING);

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);
            when(saleMapper.toResponseDto(sale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.completeSale(1L);

            assertNotNull(result);
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).registerSale(any(UpdateStockDTO.class), eq(1L));
            verify(inventoryService, times(1)).registerSale(any(UpdateStockDTO.class), eq(2L));
            verify(saleRepository, times(1)).save(any(Sale.class));
            verify(saleMapper, times(1)).toResponseDto(sale);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when sale does not exist")
        void shouldThrowResourceNotFoundExceptionWhenSaleDoesNotExist() {
            Long nonExistentId = 999L;
            when(saleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> saleService.completeSale(nonExistentId)
            );

            assertTrue(exception.getMessage().contains("Sale"));
            assertTrue(exception.getMessage().contains("Id"));
            assertTrue(exception.getMessage().contains("999"));
            verify(saleRepository, times(1)).findById(nonExistentId);
            verify(inventoryService, never()).registerSale(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw InvalidSalesStatusException when status is completed")
        void shouldThrowInvalidSalesStatusExceptionWhenStatusIsCompleted() {
            sale.setStatus(SalesStatus.COMPLETED);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

            InvalidSalesStatusException exception = assertThrows(
                    InvalidSalesStatusException.class,
                    () -> saleService.completeSale(1L)
            );

            assertTrue(exception.getMessage().contains("Cannot process sale with status"));
            assertTrue(exception.getMessage().contains("COMPLETED"));
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, never()).registerSale(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw InvalidSalesStatusException when status is cancelled")
        void shouldThrowInvalidSalesStatusExceptionWhenStatusIsCancelled() {
            sale.setStatus(SalesStatus.CANCELLED);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

            InvalidSalesStatusException exception = assertThrows(
                    InvalidSalesStatusException.class,
                    () -> saleService.completeSale(1L)
            );

            assertTrue(exception.getMessage().contains("Cannot process sale with status"));
            assertTrue(exception.getMessage().contains("CANCELLED"));
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, never()).registerSale(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw InsufficientStockException when inventory service fails")
        void shouldThrowInsufficientStockExceptionWhenInventoryServiceFails() {
            sale.setStatus(SalesStatus.PENDING);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            doThrow(new InsufficientStockException("Insufficient Stock"))
                    .when(inventoryService).registerSale(any(UpdateStockDTO.class), eq(1L));

            InsufficientStockException exception = assertThrows(
                    InsufficientStockException.class,
                    () -> saleService.completeSale(1L)
            );

            assertEquals("Insufficient Stock", exception.getMessage());
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).registerSale(any(UpdateStockDTO.class), eq(1L));
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }
    }
    @Nested
    @DisplayName("cancelSale")
    class CancelSale {

        @Test
        @DisplayName("should cancel sale successfully when status is pending")
        void shouldCancelSaleSuccessfullyWhenStatusIsPending() {
            sale.setStatus(SalesStatus.PENDING);
            Sale cancelledSale = Sale.builder()
                    .id(1L)
                    .customer(customer)
                    .status(SalesStatus.CANCELLED)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("53.98"))
                    .details(sale.getDetails())
                    .observation("Test sale")
                    .createdAt(LocalDateTime.now())
                    .createdBy(1L)
                    .build();

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            when(saleRepository.save(any(Sale.class))).thenReturn(cancelledSale);
            when(saleMapper.toResponseDto(cancelledSale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.cancelSale(1L);

            assertNotNull(result);
            assertEquals(saleResponseDTO, result);
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).releaseReservation(1L, 2);
            verify(saleRepository, times(1)).save(any(Sale.class));
            verify(saleMapper, times(1)).toResponseDto(cancelledSale);
        }

        @Test
        @DisplayName("should release reservation for all items when canceling sale")
        void shouldReleaseReservationForAllItemsWhenCancelingSale() {
            Book secondBook = Book.builder()
                    .id(2L)
                    .isbn("978-0451524935")
                    .title("1984")
                    .publishDate(LocalDate.of(1949, 6, 8))
                    .pages(328)
                    .price(new BigDecimal("15.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .author(author)
                    .build();

            SalesDetail secondDetail = SalesDetail.builder()
                    .id(2L)
                    .sale(sale)
                    .book(secondBook)
                    .quantity(3)
                    .unitPrice(new BigDecimal("15.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .lineTotal(new BigDecimal("47.97"))
                    .build();

            sale.getDetails().add(secondDetail);
            sale.setStatus(SalesStatus.PENDING);

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);
            when(saleMapper.toResponseDto(sale)).thenReturn(saleResponseDTO);

            SaleResponseDTO result = saleService.cancelSale(1L);

            assertNotNull(result);
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).releaseReservation(1L, 2);
            verify(inventoryService, times(1)).releaseReservation(2L, 3);
            verify(saleRepository, times(1)).save(any(Sale.class));
            verify(saleMapper, times(1)).toResponseDto(sale);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when sale does not exist")
        void shouldThrowResourceNotFoundExceptionWhenSaleDoesNotExist() {
            Long nonExistentId = 999L;
            when(saleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> saleService.cancelSale(nonExistentId)
            );

            assertTrue(exception.getMessage().contains("Sale"));
            assertTrue(exception.getMessage().contains("Id"));
            assertTrue(exception.getMessage().contains("999"));
            verify(saleRepository, times(1)).findById(nonExistentId);
            verify(inventoryService, never()).releaseReservation(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw InvalidSalesStatusException when status is completed")
        void shouldThrowInvalidSalesStatusExceptionWhenStatusIsCompleted() {
            sale.setStatus(SalesStatus.COMPLETED);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

            InvalidSalesStatusException exception = assertThrows(
                    InvalidSalesStatusException.class,
                    () -> saleService.cancelSale(1L)
            );

            assertTrue(exception.getMessage().contains("Cannot process sale with status"));
            assertTrue(exception.getMessage().contains("COMPLETED"));
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, never()).releaseReservation(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw InvalidSalesStatusException when status is cancelled")
        void shouldThrowInvalidSalesStatusExceptionWhenStatusIsCancelled() {
            sale.setStatus(SalesStatus.CANCELLED);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

            InvalidSalesStatusException exception = assertThrows(
                    InvalidSalesStatusException.class,
                    () -> saleService.cancelSale(1L)
            );

            assertTrue(exception.getMessage().contains("Cannot process sale with status"));
            assertTrue(exception.getMessage().contains("CANCELLED"));
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, never()).releaseReservation(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should throw InsufficientReservedStockException when release reservation fails")
        void shouldThrowInsufficientReservedStockExceptionWhenReleaseReservationFails() {
            sale.setStatus(SalesStatus.PENDING);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            doThrow(new InsufficientReservedStockException("Not enough reserved stock to release"))
                    .when(inventoryService).releaseReservation(1L, 2);

            InsufficientReservedStockException exception = assertThrows(
                    InsufficientReservedStockException.class,
                    () -> saleService.cancelSale(1L)
            );

            assertEquals("Not enough reserved stock to release", exception.getMessage());
            verify(saleRepository, times(1)).findById(1L);
            verify(inventoryService, times(1)).releaseReservation(1L, 2);
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }
    }
    @Nested
    @DisplayName("expirePendingSales")
    class ExpirePendingSales {

        @Test
        @DisplayName("should cancel all expired pending sales")
        void shouldCancelAllExpiredPendingSales() {
            Sale expiredSale1 = Sale.builder()
                    .id(1L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("53.98"))
                    .details(List.of(salesDetail))
                    .expiredAt(LocalDateTime.now().minusMinutes(10))
                    .build();

            Sale expiredSale2 = Sale.builder()
                    .id(2L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CARD)
                    .total(new BigDecimal("29.99"))
                    .details(List.of(salesDetail))
                    .expiredAt(LocalDateTime.now().minusMinutes(5))
                    .build();

            List<Sale> expiredSales = List.of(expiredSale1, expiredSale2);

            when(saleRepository.findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class)))
                    .thenReturn(expiredSales);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(expiredSale1));
            when(saleRepository.findById(2L)).thenReturn(Optional.of(expiredSale2));
            when(saleRepository.save(any(Sale.class))).thenReturn(expiredSale1, expiredSale2);
            when(saleMapper.toResponseDto(any(Sale.class))).thenReturn(saleResponseDTO);

            saleService.expirePendingSales();

            verify(saleRepository, times(1)).findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class));
            verify(saleRepository, times(1)).findById(1L);
            verify(saleRepository, times(1)).findById(2L);
            verify(inventoryService, times(2)).releaseReservation(any(), any());
            verify(saleRepository, times(2)).save(any(Sale.class));
        }

        @Test
        @DisplayName("should do nothing when no expired sales exist")
        void shouldDoNothingWhenNoExpiredSalesExist() {
            when(saleRepository.findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            saleService.expirePendingSales();

            verify(saleRepository, times(1)).findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class));
            verify(saleRepository, never()).findById(any());
            verify(inventoryService, never()).releaseReservation(any(), any());
            verify(saleRepository, never()).save(any());
            verify(saleMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("should continue processing remaining sales when one sale cancellation fails")
        void shouldContinueProcessingRemainingSalesWhenOneSaleCancellationFails() {
            Sale expiredSale1 = Sale.builder()
                    .id(1L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("53.98"))
                    .details(List.of(salesDetail))
                    .expiredAt(LocalDateTime.now().minusMinutes(10))
                    .build();

            Sale expiredSale2 = Sale.builder()
                    .id(2L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CARD)
                    .total(new BigDecimal("29.99"))
                    .details(List.of(salesDetail))
                    .expiredAt(LocalDateTime.now().minusMinutes(5))
                    .build();

            Sale expiredSale3 = Sale.builder()
                    .id(3L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.TRANSFER)
                    .total(new BigDecimal("19.99"))
                    .details(List.of(salesDetail))
                    .expiredAt(LocalDateTime.now().minusMinutes(3))
                    .build();

            List<Sale> expiredSales = List.of(expiredSale1, expiredSale2, expiredSale3);

            when(saleRepository.findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class)))
                    .thenReturn(expiredSales);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(expiredSale1));
            when(saleRepository.findById(2L)).thenThrow(new ResourceNotFoundException("Sale", "Id", 2L));
            when(saleRepository.findById(3L)).thenReturn(Optional.of(expiredSale3));
            when(saleRepository.save(any(Sale.class))).thenReturn(expiredSale1, expiredSale3);
            when(saleMapper.toResponseDto(any(Sale.class))).thenReturn(saleResponseDTO);

            saleService.expirePendingSales();

            verify(saleRepository, times(1)).findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class));
            verify(saleRepository, times(1)).findById(1L);
            verify(saleRepository, times(1)).findById(2L);
            verify(saleRepository, times(1)).findById(3L);
            verify(saleRepository, times(2)).save(any(Sale.class));
        }

        @Test
        @DisplayName("should continue processing when InvalidSalesStatusException is thrown")
        void shouldContinueProcessingWhenInvalidSalesStatusExceptionIsThrown() {
            Sale expiredSale1 = Sale.builder()
                    .id(1L)
                    .customer(customer)
                    .status(SalesStatus.COMPLETED)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("53.98"))
                    .details(List.of(salesDetail))
                    .expiredAt(LocalDateTime.now().minusMinutes(10))
                    .build();

            Sale expiredSale2 = Sale.builder()
                    .id(2L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CARD)
                    .total(new BigDecimal("29.99"))
                    .details(List.of(salesDetail))
                    .expiredAt(LocalDateTime.now().minusMinutes(5))
                    .build();

            List<Sale> expiredSales = List.of(expiredSale1, expiredSale2);

            when(saleRepository.findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class)))
                    .thenReturn(expiredSales);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(expiredSale1));
            when(saleRepository.findById(2L)).thenReturn(Optional.of(expiredSale2));
            when(saleRepository.save(any(Sale.class))).thenReturn(expiredSale2);
            when(saleMapper.toResponseDto(any(Sale.class))).thenReturn(saleResponseDTO);

            saleService.expirePendingSales();

            verify(saleRepository, times(1)).findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class));
            verify(saleRepository, times(1)).findById(1L);
            verify(saleRepository, times(1)).findById(2L);
            verify(saleRepository, times(1)).save(any(Sale.class));
        }

        @Test
        @DisplayName("should continue processing when InsufficientReservedStockException is thrown")
        void shouldContinueProcessingWhenInsufficientReservedStockExceptionIsThrown() {

            SalesDetail detail1 = SalesDetail.builder()
                    .id(10L)
                    .book(book)
                    .quantity(2)
                    .unitPrice(new BigDecimal("29.99"))
                    .discountPercent(new BigDecimal("10.00"))
                    .lineTotal(new BigDecimal("53.98"))
                    .build();

            Sale expiredSale1 = Sale.builder()
                    .id(10L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("53.98"))
                    .details(new ArrayList<>(List.of(detail1)))
                    .expiredAt(LocalDateTime.now().minusMinutes(10))
                    .build();

            detail1.setSale(expiredSale1);

            // Segunda venta que tendrá éxito (crea un libro diferente)
            Book book2 = Book.builder()
                    .id(2L)
                    .isbn("978-0451524935")
                    .title("1984")
                    .publishDate(LocalDate.of(1949, 6, 8))
                    .pages(328)
                    .price(new BigDecimal("19.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .author(author)
                    .build();

            SalesDetail detail2 = SalesDetail.builder()
                    .id(20L)
                    .book(book2)
                    .quantity(1)
                    .unitPrice(new BigDecimal("19.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .lineTotal(new BigDecimal("19.99"))
                    .build();

            Sale expiredSale2 = Sale.builder()
                    .id(20L)
                    .customer(customer)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CARD)
                    .total(new BigDecimal("19.99"))
                    .details(new ArrayList<>(List.of(detail2)))
                    .expiredAt(LocalDateTime.now().minusMinutes(5))
                    .build();

            detail2.setSale(expiredSale2);

            List<Sale> expiredSales = List.of(expiredSale1, expiredSale2);

            when(saleRepository.findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class)))
                    .thenReturn(expiredSales);
            when(saleRepository.findById(10L)).thenReturn(Optional.of(expiredSale1));
            when(saleRepository.findById(20L)).thenReturn(Optional.of(expiredSale2));

            doThrow(new InsufficientReservedStockException("Not enough reserved stock to release"))
                    .when(inventoryService).releaseReservation(1L, 2);

            doNothing().when(inventoryService).releaseReservation(2L, 1);

            when(saleRepository.save(any(Sale.class))).thenReturn(expiredSale2);
            when(saleMapper.toResponseDto(any(Sale.class))).thenReturn(saleResponseDTO);

            saleService.expirePendingSales();

            verify(saleRepository, times(1)).findByStatusAndExpiredAtBefore(eq(SalesStatus.PENDING), any(LocalDateTime.class));
            verify(saleRepository, times(1)).findById(10L);
            verify(saleRepository, times(1)).findById(20L);
            verify(inventoryService, times(1)).releaseReservation(1L, 2);
            verify(inventoryService, times(1)).releaseReservation(2L, 1);
            verify(saleRepository, times(1)).save(any(Sale.class));
        }
    }
}
