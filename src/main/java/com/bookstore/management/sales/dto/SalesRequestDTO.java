package com.bookstore.management.sales.dto;

import com.bookstore.management.sales.model.PaymentMethod;
import com.bookstore.management.sales.model.SalesDetail;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record SalesRequestDTO(

        @NotNull(message = "The customer ID is required")
        @Positive(message = "the customer ID must be negative")
        Long customerId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Items are required")
        @NotEmpty(message = "Sale must have at least one item")
        @Valid
        List<SalesDetailRequestDTO> items,

        @PositiveOrZero(message = "Discount cannot be negative")
        @DecimalMin(value = "0.0",inclusive = true)
        @DecimalMax(value = "100.0")
        BigDecimal discountPercentage,

        @Size(max = 500, message = "Observation cannot exceed 500 characters")
        String observation
) {
}
