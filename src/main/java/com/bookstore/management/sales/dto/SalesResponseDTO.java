package com.bookstore.management.sales.dto;

import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.sales.model.PaymentMethod;
import com.bookstore.management.sales.model.SalesStatus;

import java.math.BigDecimal;
import java.util.List;

public record SalesResponseDTO(
        Long id,
        CustomerSummaryDTO customer,
        SalesStatus salesStatus,
        PaymentMethod paymentMethod,
        List<SalesDetailResponseDTO> details,
        BigDecimal discountPercentCustomer,
        BigDecimal total,
        String observation
) {
}
