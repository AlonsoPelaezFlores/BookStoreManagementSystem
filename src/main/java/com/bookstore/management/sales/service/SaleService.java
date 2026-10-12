package com.bookstore.management.sales.service;

import com.bookstore.management.sales.dto.SaleRequestDTO;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.model.SalesStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface SaleService {

    SaleResponseDTO findById(Long id);
    Page<SaleResponseDTO> findAll(Pageable pageable);
    List<SaleResponseDTO> findByCustomerId(Long customerId);
    List<SaleResponseDTO> findByStatus(SalesStatus status);
    List<SaleResponseDTO> findByDateRange(LocalDate start, LocalDate end);
    SaleResponseDTO createSale(SaleRequestDTO saleRequestDTO);
    SaleResponseDTO completeSale(Long saleId);
    SaleResponseDTO cancelSale(Long saleId);
}
