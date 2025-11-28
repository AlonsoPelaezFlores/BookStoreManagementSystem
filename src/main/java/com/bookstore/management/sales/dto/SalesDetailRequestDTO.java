package com.bookstore.management.sales.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SalesDetailRequestDTO(

        @NotNull(message = "Book ID cannot be null")
        @Positive(message = "Book ID must be positive ")
        Long bookId,

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @DecimalMin(value = "0.0", inclusive = true)
        @DecimalMax(value = "100.0")
        BigDecimal discountPercentage
) {
}
