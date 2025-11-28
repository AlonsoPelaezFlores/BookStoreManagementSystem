package com.bookstore.management.customer.controller;

import com.bookstore.management.customer.dto.CustomerCreateDTO;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.customer.service.CustomerService;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Customer controller test")
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerCreateDTO customerCreateDTO;
    private Customer customer;
    private Customer anotherCustomer;

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
                .birthDate(LocalDate.of(1985, 5, 15))
                .build();

        customerCreateDTO = CustomerCreateDTO.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@email.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
    }
    @Nested
    @DisplayName("Get All Customers")
    class GetAllCustomers {
        @Test
        @DisplayName("Should return all customers when customers exist")
        void shouldReturnAllCustomersWhenCustomersExist() throws Exception {

            List<Customer> customers = Arrays.asList(customer, anotherCustomer);

            when(customerService.findAll()).thenReturn(customers);

            mockMvc.perform(get("/api/customers"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("John"))
                    .andExpect(jsonPath("$[0].surname").value("Doe"))
                    .andExpect(jsonPath("$[0].email").value("john.doe@email.com"))
                    .andExpect(jsonPath("$[0].birthDate").value("1990-01-01"))

                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].name").value("Jane"))
                    .andExpect(jsonPath("$[1].surname").value("Smith"))
                    .andExpect(jsonPath("$[1].email").value("jane.smith@email.com"))
                    .andExpect(jsonPath("$[1].birthDate").value("1985-05-15"));
        }
        @Test
        @DisplayName("Should return empty list when no customers exist")
        void shouldReturnEmptyListWhenNoCustomersExist() throws Exception {
            when(customerService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/customers"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
    @Nested
    @DisplayName("Get Customer By Id")
    class GetCustomerById {

        @Test
        @DisplayName("Should return customer when customer exists")
        void shouldReturnCustomerWhenCustomerExists() throws Exception {

            Long customerId = 1L;
            when(customerService.findById(customerId)).thenReturn(customer);

            mockMvc.perform(get("/api/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("John"))
                    .andExpect(jsonPath("$.surname").value("Doe"))
                    .andExpect(jsonPath("$.email").value("john.doe@email.com"));
        }

        @Test
        @DisplayName("Should return not found when customer does not exist")
        void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {

            Long customerId = 999L;
            when(customerService.findById(customerId))
                    .thenThrow(new ResourceNotFoundException("Customer", "Id", customerId));

            mockMvc.perform(get("/api/customers/{id}", customerId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return bad request when id is negative")
        void shouldReturnBadRequestWhenIdIsNegative() throws Exception {

            mockMvc.perform(get("/api/customers/{id}", -1L))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("Create Customer")
    class CreateCustomer {

        @Test
        @DisplayName("Should create customer when valid data provided")
        void shouldCreateCustomerWhenValidDataProvided() throws Exception {

            when(customerService.create(any(CustomerCreateDTO.class))).thenReturn(customer);

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("Should return bad request when name is missing")
        void shouldReturnBadRequestWhenNameIsMissing() throws Exception {

            customerCreateDTO.setName(null);

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when email is invalid")
        void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {

            customerCreateDTO.setEmail("invalid-email");

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("Update Customer")
    class UpdateCustomer {

        @Test
        @DisplayName("Should update customer when valid data provided")
        void shouldUpdateCustomerWhenValidDataProvided() throws Exception {

            Long customerId = 1L;
            CustomerCreateDTO updateDto = CustomerCreateDTO.builder()
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

            when(customerService.update(any(CustomerCreateDTO.class), eq(customerId))).thenReturn(updatedCustomer);

            mockMvc.perform(put("/api/customers/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(customerId));
        }

        @Test
        @DisplayName("Should return not found when customer to update does not exist")
        void shouldReturnNotFoundWhenCustomerToUpdateDoesNotExist() throws Exception {

            Long customerId = 999L;
            when(customerService.update(any(CustomerCreateDTO.class), eq(customerId)))
                    .thenThrow(new ResourceNotFoundException("Customer", "Id", customerId));

            mockMvc.perform(put("/api/customers/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return bad request when id is negative")
        void shouldReturnBadRequestWhenIdIsNegative() throws Exception {

            mockMvc.perform(put("/api/customers/{id}", -1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when request body is invalid")
        void shouldReturnBadRequestWhenRequestBodyIsInvalid() throws Exception {

            customerCreateDTO.setEmail("invalid-email");

            mockMvc.perform(put("/api/customers/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerCreateDTO)))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("Delete Customer")
    class DeleteCustomer {

        @Test
        @DisplayName("Should delete customer when customer exists")
        void shouldDeleteCustomerWhenCustomerExists() throws Exception {

            Long customerId = 1L;

            mockMvc.perform(delete("/api/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Customer deleted successfully"));
        }

        @Test
        @DisplayName("Should return not found when customer to delete does not exist")
        void shouldReturnNotFoundWhenCustomerToDeleteDoesNotExist() throws Exception {

            Long customerId = 999L;
            doThrow(new ResourceNotFoundException("Customer", "Id", customerId))
                    .when(customerService).deleteById(customerId);

            mockMvc.perform(delete("/api/customers/{id}", customerId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return bad request when id is negative")
        void shouldReturnBadRequestWhenIdIsNegative() throws Exception {

            mockMvc.perform(delete("/api/customers/{id}", -1L))
                    .andExpect(status().isBadRequest());
        }
    }
}
