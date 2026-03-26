package com.bookstore.management.sale.mapper;


import com.bookstore.management.book.mapper.AuthorMapperImpl;
import com.bookstore.management.book.mapper.BookMapperImpl;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.customer.mapper.CustomerMapperImpl;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.mapper.SaleMapper;
import com.bookstore.management.sales.mapper.SaleMapperImpl;
import com.bookstore.management.sales.model.PaymentMethod;
import com.bookstore.management.sales.model.Sale;
import com.bookstore.management.sales.model.SalesDetail;
import com.bookstore.management.sales.model.SalesStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        SaleMapperImpl.class,
        CustomerMapperImpl.class,
        BookMapperImpl.class,
        AuthorMapperImpl.class,
})
@DisplayName("Sale Mapper Tests")
class SaleMapperTest {

    @Autowired
    private SaleMapper saleMapper ;

    @Nested
    @DisplayName("toResponseDto() - Single Sale Mapping")
    class ToResponseDtoTests {

        @Test
        @DisplayName("should map sale with all fields correctly including customer and details")
        void shouldMapSaleWithAllFieldsCorrectlyIncludingCustomerAndDetails() {
            Customer customer = Customer.builder()
                    .id(1L)
                    .name("John")
                    .surname("Doe")
                    .email("john.doe@example.com")
                    .birthDate(LocalDate.of(1990, 5, 15))
                    .build();

            Book book = Book.builder()
                    .id(1L)
                    .title("Clean Code")
                    .isbn("978-0132350884")
                    .price(new BigDecimal("45.99"))
                    .author(new Author("Robert C. Martin","Spain",LocalDate.of(1990, 5, 15)))
                    .build();

            SalesDetail detail = SalesDetail.builder()
                    .id(1L)
                    .book(book)
                    .quantity(2)
                    .unitPrice(new BigDecimal("45.99"))
                    .discountPercent(new BigDecimal("10.00"))
                    .lineTotal(new BigDecimal("82.78"))
                    .build();

            Sale sale = Sale.builder()
                    .id(1L)
                    .customer(customer)
                    .status(SalesStatus.COMPLETED)
                    .paymentMethod(PaymentMethod.CARD)
                    .total(new BigDecimal("82.78"))
                    .details(List.of(detail))
                    .observation("Customer requested gift wrapping")
                    .createdAt(LocalDateTime.now())
                    .createdBy(1L)
                    .build();

            SaleResponseDTO result = saleMapper.toResponseDto(sale);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.status()).isEqualTo(SalesStatus.COMPLETED);
            assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.CARD);
            assertThat(result.total()).isEqualByComparingTo(new BigDecimal("82.78"));
            assertThat(result.observation()).isEqualTo("Customer requested gift wrapping");

            assertThat(result.customer()).isNotNull();
            assertThat(result.customer().id()).isEqualTo(1L);
            assertThat(result.customer().fullName()).isEqualTo("John Doe");
            assertThat(result.customer().email()).isEqualTo("john.doe@example.com");

            assertThat(result.details()).hasSize(1);
            assertThat(result.details().get(0).id()).isEqualTo(1L);
            assertThat(result.details().get(0).quantity()).isEqualTo(2);
            assertThat(result.details().get(0).book().title()).isEqualTo("Clean Code");
        }

        @Test
        @DisplayName("should map sale without customer when customer is null")
        void shouldMapSaleWithoutCustomerWhenCustomerIsNull() {
            Book book = Book.builder()
                    .id(1L)
                    .title("Design Patterns")
                    .isbn("978-0201633610")
                    .price(new BigDecimal("54.99"))
                    .author(new Author("Robert C. Martin","Spain",LocalDate.of(2000, 5, 15)))
                    .build();

            SalesDetail detail = SalesDetail.builder()
                    .id(1L)
                    .book(book)
                    .quantity(1)
                    .unitPrice(new BigDecimal("54.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .lineTotal(new BigDecimal("54.99"))
                    .build();

            Sale sale = Sale.builder()
                    .id(2L)
                    .customer(null)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("54.99"))
                    .details(List.of(detail))
                    .createdAt(LocalDateTime.now())
                    .createdBy(1L)
                    .build();

            SaleResponseDTO result = saleMapper.toResponseDto(sale);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(2L);
            assertThat(result.customer()).isNull();
            assertThat(result.status()).isEqualTo(SalesStatus.PENDING);
            assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.CASH);
            assertThat(result.total()).isEqualByComparingTo(new BigDecimal("54.99"));
            assertThat(result.details()).hasSize(1);
        }

        @Test
        @DisplayName("should map sale with empty details list")
        void shouldMapSaleWithEmptyDetailsList() {
            Customer customer = Customer.builder()
                    .id(1L)
                    .name("Jane")
                    .surname("Smith")
                    .email("jane.smith@example.com")
                    .birthDate(LocalDate.of(1985, 8, 20))
                    .build();

            Sale sale = Sale.builder()
                    .id(3L)
                    .customer(customer)
                    .status(SalesStatus.CANCELLED)
                    .paymentMethod(PaymentMethod.CARD)
                    .total(BigDecimal.ZERO)
                    .details(new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .createdBy(1L)
                    .build();

            SaleResponseDTO result = saleMapper.toResponseDto(sale);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(3L);
            assertThat(result.customer()).isNotNull();
            assertThat(result.customer().fullName()).isEqualTo("Jane Smith");
            assertThat(result.details()).isEmpty();
            assertThat(result.status()).isEqualTo(SalesStatus.CANCELLED);
        }

        @Test
        @DisplayName("should map sale with multiple details correctly")
        void shouldMapSaleWithMultipleDetailsCorrectly() {
            Customer customer = Customer.builder()
                    .id(1L)
                    .name("Alice")
                    .surname("Johnson")
                    .email("alice.j@example.com")
                    .birthDate(LocalDate.of(1992, 3, 10))
                    .build();

            Book book1 = Book.builder()
                    .id(1L)
                    .title("Effective Java")
                    .isbn("978-0134685991")
                    .price(new BigDecimal("47.99"))
                    .author(new Author("Joshua Blosh","Spain",LocalDate.of(1990, 5, 15)))
                    .build();

            Book book2 = Book.builder()
                    .id(2L)
                    .title("Spring in Action")
                    .isbn("978-1617294945")
                    .price(new BigDecimal("39.99"))
                    .author(new Author("Craig Walls","Spain",LocalDate.of(1800, 5, 15)))
                    .build();

            SalesDetail detail1 = SalesDetail.builder()
                    .id(1L)
                    .book(book1)
                    .quantity(1)
                    .unitPrice(new BigDecimal("47.99"))
                    .discountPercent(new BigDecimal("5.00"))
                    .lineTotal(new BigDecimal("45.59"))
                    .build();

            SalesDetail detail2 = SalesDetail.builder()
                    .id(2L)
                    .book(book2)
                    .quantity(2)
                    .unitPrice(new BigDecimal("39.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .lineTotal(new BigDecimal("79.98"))
                    .build();

            Sale sale = Sale.builder()
                    .id(4L)
                    .customer(customer)
                    .status(SalesStatus.COMPLETED)
                    .paymentMethod(PaymentMethod.CARD)
                    .total(new BigDecimal("125.57"))
                    .details(List.of(detail1, detail2))
                    .createdAt(LocalDateTime.now())
                    .createdBy(1L)
                    .build();

            SaleResponseDTO result = saleMapper.toResponseDto(sale);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(4L);
            assertThat(result.details()).hasSize(2);
            assertThat(result.details().get(0).book().title()).isEqualTo("Effective Java");
            assertThat(result.details().get(1).book().title()).isEqualTo("Spring in Action");
            assertThat(result.total()).isEqualByComparingTo(new BigDecimal("125.57"));
        }

        @Test
        @DisplayName("should map sale with null observation")
        void shouldMapSaleWithNullObservation() {
            Book book = Book.builder()
                    .id(1L)
                    .title("Test Book")
                    .isbn("978-0000000000")
                    .price(new BigDecimal("29.99"))
                    .author(new Author("Robert C. Martin","Spain",LocalDate.of(1990, 5, 15)))
                    .build();

            SalesDetail detail = SalesDetail.builder()
                    .id(1L)
                    .book(book)
                    .quantity(1)
                    .unitPrice(new BigDecimal("29.99"))
                    .discountPercent(BigDecimal.ZERO)
                    .lineTotal(new BigDecimal("29.99"))
                    .build();

            Sale sale = Sale.builder()
                    .id(5L)
                    .customer(null)
                    .status(SalesStatus.PENDING)
                    .paymentMethod(PaymentMethod.CASH)
                    .total(new BigDecimal("29.99"))
                    .details(List.of(detail))
                    .observation(null)
                    .createdAt(LocalDateTime.now())
                    .createdBy(1L)
                    .build();

            SaleResponseDTO result = saleMapper.toResponseDto(sale);

            assertThat(result).isNotNull();
            assertThat(result.observation()).isNull();
        }

        @Test
        @DisplayName("should return null when sale is null")
        void shouldReturnNullWhenSaleIsNull() {
            SaleResponseDTO result = saleMapper.toResponseDto(null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all payment method enum values correctly")
        void shouldMapAllPaymentMethodEnumValuesCorrectly() {
            Sale saleWithCash = createBasicSale(1L, PaymentMethod.CASH);
            Sale saleWithCard = createBasicSale(2L, PaymentMethod.CARD);
            Sale saleWithDebit = createBasicSale(3L, PaymentMethod.CARD);

            SaleResponseDTO cashResult = saleMapper.toResponseDto(saleWithCash);
            SaleResponseDTO cardResult = saleMapper.toResponseDto(saleWithCard);
            SaleResponseDTO debitResult = saleMapper.toResponseDto(saleWithDebit);

            assertThat(cashResult.paymentMethod()).isEqualTo(PaymentMethod.CASH);
            assertThat(cardResult.paymentMethod()).isEqualTo(PaymentMethod.CARD);
            assertThat(debitResult.paymentMethod()).isEqualTo(PaymentMethod.CARD);
        }

        @Test
        @DisplayName("should map all sales status enum values correctly")
        void shouldMapAllSalesStatusEnumValuesCorrectly() {
            Sale pendingSale = createBasicSaleWithStatus(1L, SalesStatus.PENDING);
            Sale completedSale = createBasicSaleWithStatus(2L, SalesStatus.COMPLETED);
            Sale cancelledSale = createBasicSaleWithStatus(3L, SalesStatus.CANCELLED);

            SaleResponseDTO pendingResult = saleMapper.toResponseDto(pendingSale);
            SaleResponseDTO completedResult = saleMapper.toResponseDto(completedSale);
            SaleResponseDTO cancelledResult = saleMapper.toResponseDto(cancelledSale);

            assertThat(pendingResult.status()).isEqualTo(SalesStatus.PENDING);
            assertThat(completedResult.status()).isEqualTo(SalesStatus.COMPLETED);
            assertThat(cancelledResult.status()).isEqualTo(SalesStatus.CANCELLED);
        }

        @Test
        @DisplayName("should concatenate customer name and surname correctly in full name")
        void shouldConcatenateCustomerNameAndSurnameCorrectlyInFullName() {
            Customer customer = Customer.builder()
                    .id(1L)
                    .name("María")
                    .surname("García López")
                    .email("maria.garcia@example.com")
                    .birthDate(LocalDate.of(1988, 12, 5))
                    .build();

            Sale sale = createBasicSale(1L, PaymentMethod.CASH);
            sale.setCustomer(customer);

            SaleResponseDTO result = saleMapper.toResponseDto(sale);

            assertThat(result.customer()).isNotNull();
            assertThat(result.customer().fullName()).isEqualTo("María García López");
        }
    }

    @Nested
    @DisplayName("toResponseDtoList() - Multiple Sales Mapping")
    class ToResponseDtoListTests {

        @Test
        @DisplayName("should map list of sales correctly")
        void shouldMapListOfSalesCorrectly() {
            Sale sale1 = createBasicSale(1L, PaymentMethod.CASH);
            Sale sale2 = createBasicSale(2L, PaymentMethod.CARD);
            Sale sale3 = createBasicSale(3L, PaymentMethod.CARD);

            List<Sale> sales = List.of(sale1, sale2, sale3);

            List<SaleResponseDTO> result = saleMapper.toResponseDtoList(sales);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(2).id()).isEqualTo(3L);
            assertThat(result.get(0).paymentMethod()).isEqualTo(PaymentMethod.CASH);
            assertThat(result.get(1).paymentMethod()).isEqualTo(PaymentMethod.CARD);
            assertThat(result.get(2).paymentMethod()).isEqualTo(PaymentMethod.CARD);
        }

        @Test
        @DisplayName("should return empty list when input list is empty")
        void shouldReturnEmptyListWhenInputListIsEmpty() {
            List<Sale> emptySales = new ArrayList<>();

            List<SaleResponseDTO> result = saleMapper.toResponseDtoList(emptySales);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return null when input list is null")
        void shouldReturnNullWhenInputListIsNull() {
            List<SaleResponseDTO> result = saleMapper.toResponseDtoList(null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map list with mixed customer presence correctly")
        void shouldMapListWithMixedCustomerPresenceCorrectly() {
            Customer customer = Customer.builder()
                    .id(1L)
                    .name("Bob")
                    .surname("Williams")
                    .email("bob.w@example.com")
                    .birthDate(LocalDate.of(1995, 7, 22))
                    .build();

            Sale saleWithCustomer = createBasicSale(1L, PaymentMethod.CASH);
            saleWithCustomer.setCustomer(customer);

            Sale saleWithoutCustomer = createBasicSale(2L, PaymentMethod.CARD);
            saleWithoutCustomer.setCustomer(null);

            List<Sale> sales = List.of(saleWithCustomer, saleWithoutCustomer);

            List<SaleResponseDTO> result = saleMapper.toResponseDtoList(sales);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).customer()).isNotNull();
            assertThat(result.get(0).customer().fullName()).isEqualTo("Bob Williams");
            assertThat(result.get(1).customer()).isNull();
        }
    }

    private Sale createBasicSale(Long id, PaymentMethod paymentMethod) {
        Book book = Book.builder()
                .id(1L)
                .title("Sample Book")
                .isbn("978-0000000000")
                .price(new BigDecimal("25.00"))
                .author(new Author("Sample Author","Spain",LocalDate.of(1990, 5, 15)))
                .build();

        SalesDetail detail = SalesDetail.builder()
                .id(1L)
                .book(book)
                .quantity(1)
                .unitPrice(new BigDecimal("25.00"))
                .discountPercent(BigDecimal.ZERO)
                .lineTotal(new BigDecimal("25.00"))
                .build();

        return Sale.builder()
                .id(id)
                .customer(null)
                .status(SalesStatus.PENDING)
                .paymentMethod(paymentMethod)
                .total(new BigDecimal("25.00"))
                .details(List.of(detail))
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .build();
    }

    private Sale createBasicSaleWithStatus(Long id, SalesStatus status) {
        Book book = Book.builder()
                .id(1L)
                .title("Sample Book")
                .isbn("978-0000000000")
                .price(new BigDecimal("25.00"))
                .author(new Author("Sample author","Spain",LocalDate.of(1990, 5, 15)))
                .build();

        SalesDetail detail = SalesDetail.builder()
                .id(1L)
                .book(book)
                .quantity(1)
                .unitPrice(new BigDecimal("25.00"))
                .discountPercent(BigDecimal.ZERO)
                .lineTotal(new BigDecimal("25.00"))
                .build();

        return Sale.builder()
                .id(id)
                .customer(null)
                .status(status)
                .paymentMethod(PaymentMethod.CASH)
                .total(new BigDecimal("25.00"))
                .details(List.of(detail))
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .build();
    }
}