package com.bookstore.management.customers.service;

import com.bookstore.management.customers.dto.CustomerDto;
import com.bookstore.management.customers.model.Customer;
import com.bookstore.management.customers.repository.CustomerRepository;
import com.bookstore.management.shared.exception.custom.CustomerNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
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

    private Customer customer;
    private Customer anotherCustomer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@email.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        anotherCustomer = Customer.builder()
                .id(2L)
                .name("Jane")
                .surname("Smith")
                .email("jane.smith@email.com")
                .birthDate(LocalDate.of(1985,5,15))
                .build();

        customerDto = CustomerDto.builder()
                .name("John")
                .surname("Doe")
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
            when(customerRepository.findAll()).thenReturn(customers);

            List<Customer> result = customerService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(customers);
            verify(customerRepository).findAll();
        }
        @Test
        @DisplayName("Should return empty list when no customers exist")
        void shouldReturnEmptyListWhenNoCustomersExist() {

            when(customerRepository.findAll()).thenReturn(Arrays.asList());

            List<Customer> result = customerService.findAll();

            assertThat(result).isEmpty();
            verify(customerRepository).findAll();
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

            Customer result = customerService.findById(customerId);

            assertThat(result).isEqualTo(customer);
            verify(customerRepository).findById(customerId);
        }

        @Test
        @DisplayName("Should throw exception when customer does not exist")
        void shouldThrowExceptionWhenCustomerDoesNotExist() {

            Long customerId = 999L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.findById(customerId))
                    .isInstanceOf(CustomerNotFoundException.class)
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

            Customer result = customerService.create(customerDto);

            assertThat(result).isEqualTo(customer);
            assertThat(result.getName()).isEqualTo(customerDto.getName());
            assertThat(result.getSurname()).isEqualTo(customerDto.getSurname());
            assertThat(result.getEmail()).isEqualTo(customerDto.getEmail());
            assertThat(result.getBirthDate()).isEqualTo(customerDto.getBirthDate());

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
            CustomerDto updateDto = CustomerDto.builder()
                    .name("Updated John")
                    .surname("Updated Doe")
                    .email("updated.john@email.com")
                    .birthDate(LocalDate.of(1991, 2, 2))
                    .build();

            Customer updatedCustomer = Customer.builder()
                    .id(customerId)
                    .name(updateDto.getName())
                    .surname(updateDto.getSurname())
                    .email(updateDto.getEmail())
                    .birthDate(updateDto.getBirthDate())
                    .build();

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

            Customer result = customerService.update(updateDto, customerId);

            assertThat(result.getName()).isEqualTo(updateDto.getName());
            assertThat(result.getSurname()).isEqualTo(updateDto.getSurname());
            assertThat(result.getEmail()).isEqualTo(updateDto.getEmail());
            assertThat(result.getBirthDate()).isEqualTo(updateDto.getBirthDate());

            verify(customerRepository).findById(customerId);
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should throw exception when customer to update does not exist")
        void shouldThrowExceptionWhenCustomerToUpdateDoesNotExist() {

            Long customerId = 999L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.update(customerDto, customerId))
                    .isInstanceOf(CustomerNotFoundException.class)
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
            when(customerRepository.existsById(customerId)).thenReturn(true);

            customerService.deleteById(customerId);

            verify(customerRepository).existsById(customerId);
            verify(customerRepository).deleteById(customerId);
        }

        @Test
        @DisplayName("Should throw exception when customer to delete does not exist")
        void shouldThrowExceptionWhenCustomerToDeleteDoesNotExist() {

            Long customerId = 999L;
            when(customerRepository.existsById(customerId)).thenReturn(false);

            assertThatThrownBy(() -> customerService.deleteById(customerId))
                    .isInstanceOf(CustomerNotFoundException.class)
                    .hasMessageContaining("Customer")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

            verify(customerRepository).existsById(customerId);
            verify(customerRepository, never()).deleteById(customerId);
        }
    }
}
