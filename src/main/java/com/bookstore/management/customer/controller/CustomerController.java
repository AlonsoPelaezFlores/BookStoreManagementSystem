package com.bookstore.management.customer.controller;

import com.bookstore.management.customer.dto.CustomerCreateDTO;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Customers", description = "Customer management")
@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    @GetMapping
    public ResponseEntity<List<CustomerSummaryDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<CustomerSummaryDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }
    @PostMapping
    public ResponseEntity<CustomerSummaryDTO> create(@RequestBody @Valid CustomerCreateDTO customerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(customerDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CustomerSummaryDTO> update(@RequestBody @Valid CustomerCreateDTO customerDto, @PathVariable @Positive Long id) {
        return ResponseEntity.ok(customerService.update(customerDto,id));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @Positive Long id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
