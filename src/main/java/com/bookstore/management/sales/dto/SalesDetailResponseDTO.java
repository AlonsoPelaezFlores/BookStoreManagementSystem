package com.bookstore.management.sales.dto;

import com.bookstore.management.book.dto.BookSummaryDTO;
import lombok.Builder;

import java.math.BigDecimal;

public record SalesDetailResponseDTO(
        Long id,
        BookSummaryDTO book,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountPercent,
        BigDecimal lineTotal
) {
}
