package com.bookstore.management.sales.dto;

import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.sales.model.PaymentMethod;
import com.bookstore.management.sales.model.SalesStatus;

import java.math.BigDecimal;
import java.util.List;

public record SaleResponseDTO(
        Long id,
        CustomerSummaryDTO customer,
        SalesStatus status,
        PaymentMethod paymentMethod,
        List<SalesDetailResponseDTO> details,
        BigDecimal total,
        String observation
) {
}
