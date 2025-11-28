package com.bookstore.management.customer.dto;

import jakarta.validation.constraints.*;

public record CustomerSummaryDTO(

        @Positive(message = "Id must be positive")
        @NotNull(message = "Id cannot be null")
        Long id,

        @NotBlank(message = "Full name is required")
        String fullName,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email
) {

}
