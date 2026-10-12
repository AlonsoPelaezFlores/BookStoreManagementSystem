package com.bookstore.management.customer.service;

import com.bookstore.management.customer.dto.CreateCustomerDTO;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.customer.mapper.CustomerMapper;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.customer.repository.CustomerRepository;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerService customerService;
    @Spy
    private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    private Customer customer;
    private Customer anotherCustomer;
    private CreateCustomerDTO createCustomerDTO;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        anotherCustomer = Customer.builder()
                .id(2L)
                .name("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .birthDate(LocalDate.of(1985,5,15))
                .build();

        createCustomerDTO = CreateCustomerDTO.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
    }
    @Nested
    @DisplayName("Find All")
    class FindAll {
        @Test
        @DisplayName("Should return all customers when customers exist")
        void shouldReturnAllCustomersWhenCustomersExist() {

            List<Customer> customers = Arrays.asList(customer,anotherCustomer);
            Pageable pageable = PageRequest.of(0, 10);
            when(customerRepository.findAll(pageable)).thenReturn(new PageImpl<>(customers, pageable, 2));

            Page<CustomerSummaryDTO> result = customerService.findAll(pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            verify(customerRepository).findAll(pageable);
        }
        @Test
        @DisplayName("Should return empty list when no customers exist")
        void shouldReturnEmptyListWhenNoCustomersExist() {

            Pageable pageable = PageRequest.of(0, 10);
            when(customerRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

            Page<CustomerSummaryDTO> result = customerService.findAll(pageable);

            assertThat(result.getContent()).isEmpty();
            verify(customerRepository).findAll(pageable);
        }
    }
    @Nested
    @DisplayName("Find By Id")
    class FindById {

        @Test
        @DisplayName("Should return customer when customer exists")
        void shouldReturnCustomerWhenCustomerExists() {

            Long customerId = 1L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            CustomerSummaryDTO result = customerService.findById(customerId);

            assertThat(result.id()).isEqualTo(customer.getId());
            assertThat(result.name()).isEqualTo(customer.getName());
            assertThat(result.lastName()).isEqualTo(customer.getLastName());
            assertThat(result.email()).isEqualTo(customer.getEmail());

            verify(customerRepository).findById(customerId);
        }

        @Test
        @DisplayName("Should throw exception when customer does not exist")
        void shouldThrowExceptionWhenCustomerDoesNotExist() {

            Long customerId = 999L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.findById(customerId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

            verify(customerRepository).findById(customerId);
        }
    }
    @Nested
    @DisplayName("Create")
    class Create {

        @Test
        @DisplayName("Should create customer when valid data provided")
        void shouldCreateCustomerWhenValidDataProvided() {

            when(customerRepository.save(any(Customer.class))).thenReturn(customer);

            CustomerSummaryDTO result = customerService.create(createCustomerDTO);

            assertThat(result.name()).isEqualTo(createCustomerDTO.getName());
            assertThat(result.lastName()).isEqualTo(createCustomerDTO.getLastName());
            assertThat(result.email()).isEqualTo(createCustomerDTO.getEmail());

            verify(customerRepository).save(any(Customer.class));
        }
    }
    @Nested
    @DisplayName("Update")
    class Update {

        @Test
        @DisplayName("Should update customer when customer exists")
        void shouldUpdateCustomerWhenCustomerExists() {

            Long customerId = 1L;
            CreateCustomerDTO updateDto = CreateCustomerDTO.builder()
                    .name("Updated John")
                    .lastName("Updated Doe")
                    .email("updated.john@email.com")
                    .birthDate(LocalDate.of(1991, 2, 2))
                    .build();

            Customer updatedCustomer = Customer.builder()
                    .id(customerId)
                    .name(updateDto.getName())
                    .lastName(updateDto.getLastName())
                    .email(updateDto.getEmail())
                    .birthDate(updateDto.getBirthDate())
                    .build();

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

            CustomerSummaryDTO result = customerService.update(updateDto, customerId);

            assertThat(result.name()).isEqualTo(updateDto.getName());
            assertThat(result.lastName()).isEqualTo(updateDto.getLastName());
            assertThat(result.email()).isEqualTo(updateDto.getEmail());

            verify(customerRepository).findById(customerId);
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should throw exception when customer to update does not exist")
        void shouldThrowExceptionWhenCustomerToUpdateDoesNotExist() {

            Long customerId = 999L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.update(createCustomerDTO, customerId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

            verify(customerRepository).findById(customerId);
            verify(customerRepository, never()).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Delete By Id")
    class DeleteById {

        @Test
        @DisplayName("Should delete customer when customer exists")
        void shouldDeleteCustomerWhenCustomerExists() {

            Long customerId = 1L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            customerService.deleteById(customerId);

            verify(customerRepository).findById(customerId);
            verify(customerRepository).deleteById(customerId);
        }

        @Test
        @DisplayName("Should throw exception when customer to delete does not exist")
        void shouldThrowExceptionWhenCustomerToDeleteDoesNotExist() {

            Long customerId = 999L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.deleteById(customerId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

            verify(customerRepository).findById(customerId);
            verify(customerRepository, never()).deleteById(customerId);
        }
    }
}
