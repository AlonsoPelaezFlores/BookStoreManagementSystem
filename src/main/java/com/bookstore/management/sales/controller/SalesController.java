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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Sales")
@RestController
@RequestMapping(value = "/api/sales")
@RequiredArgsConstructor
@Validated
public class SalesController {

    private final SaleService saleService;
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        return ResponseEntity.ok(saleService.findAll());
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(saleService.findById(id));
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByCustomer(@PathVariable @Positive Long customerId) {
        return ResponseEntity.ok(saleService.findByCustomerId(customerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByStatus(@PathVariable SalesStatus status) {
        return ResponseEntity.ok(saleService.findByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(saleService.findByDateRange(start, end));
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
