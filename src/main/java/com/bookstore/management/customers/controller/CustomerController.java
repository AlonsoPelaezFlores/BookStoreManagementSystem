package com.bookstore.management.customers.controller;

import com.bookstore.management.customers.dto.CustomerDto;
import com.bookstore.management.customers.dto.CustomerResponse;
import com.bookstore.management.customers.model.Customer;
import com.bookstore.management.customers.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    @GetMapping
    public ResponseEntity<List<Customer>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CustomerDto customerDto) {
        Customer customer = customerService.create(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomerResponse(customer.getId()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid CustomerDto customerDto, @PathVariable @Positive Long id) {
        Customer customer = customerService.update(customerDto, id);
        return ResponseEntity.ok(new CustomerResponse(customer.getId()));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @Positive Long id) {
        customerService.deleteById(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }
}
