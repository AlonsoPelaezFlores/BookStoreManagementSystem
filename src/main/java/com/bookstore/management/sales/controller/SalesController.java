package com.bookstore.management.sales.controller;

import com.bookstore.management.sales.dto.SaleRequestDTO;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.model.SalesStatus;
import com.bookstore.management.sales.service.SaleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Sales")
@RestController
@RequestMapping(value = "/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final static int DEFAULT_SIZE = 10;
    private final static String DEFAULT_SORT = "id";

    private final SaleService saleService;
    @GetMapping
    public ResponseEntity<Page<SaleResponseDTO>> getAllSales(
            @PageableDefault(size = DEFAULT_SIZE, sort = DEFAULT_SORT) Pageable pageable) {
        return ResponseEntity.ok(saleService.findAll(pageable));
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(saleService.findById(id));
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<SaleResponseDTO>> getSalesByCustomer(
            @PathVariable @Positive Long customerId,
            @PageableDefault(size = DEFAULT_SIZE, sort = DEFAULT_SORT) Pageable pageable) {
        return ResponseEntity.ok(saleService.findByCustomerId(customerId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<SaleResponseDTO>> getSalesByStatus(
            @PathVariable SalesStatus status,
            @PageableDefault(size = DEFAULT_SIZE, sort = DEFAULT_SORT) Pageable pageable) {
        return ResponseEntity.ok(saleService.findByStatus(status, pageable));
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<SaleResponseDTO>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PageableDefault(size = DEFAULT_SIZE, sort = DEFAULT_SORT) Pageable pageable) {
        return ResponseEntity.ok(saleService.findByDateRange(start, end, pageable));
    }

    @PostMapping
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody @Valid SaleRequestDTO saleRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.createSale(saleRequestDTO));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<SaleResponseDTO> completeSale(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(saleService.completeSale(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(saleService.cancelSale(id));
    }
}
