package com.bookstore.management.sales.service;

import com.bookstore.management.sales.dto.SaleRequestDTO;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.model.SalesStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SaleService {

    SaleResponseDTO findById(Long id);
    Page<SaleResponseDTO> findAll(Pageable pageable);
    Page<SaleResponseDTO> findByCustomerId(Long customerId, Pageable pageable);
    Page<SaleResponseDTO> findByStatus(SalesStatus status, Pageable pageable);
    Page<SaleResponseDTO> findByDateRange(LocalDate start, LocalDate end, Pageable pageable);
    SaleResponseDTO createSale(SaleRequestDTO saleRequestDTO);
    SaleResponseDTO completeSale(Long saleId);
    SaleResponseDTO cancelSale(Long saleId);
}
