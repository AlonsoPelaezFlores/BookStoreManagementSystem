package com.bookstore.management.customer.controller;

import com.bookstore.management.customer.dto.CreateCustomerDTO;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management")
@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {

    private final static int DEFAULT_SIZE = 10;
    private final static String DEFAULT_SORT = "id";
    private final CustomerService customerService;
    @GetMapping
    public ResponseEntity<Page<CustomerSummaryDTO>> findAll(
            @PageableDefault(size = DEFAULT_SIZE, sort = DEFAULT_SORT) Pageable pageable) {
        return ResponseEntity.ok(customerService.findAll(pageable));
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<CustomerSummaryDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }
    @PostMapping
    public ResponseEntity<CustomerSummaryDTO> create(@RequestBody @Valid CreateCustomerDTO customerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(customerDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CustomerSummaryDTO> update(@RequestBody @Valid CreateCustomerDTO customerDto, @PathVariable @Positive Long id) {
        return ResponseEntity.ok(customerService.update(customerDto,id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @Positive Long id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
