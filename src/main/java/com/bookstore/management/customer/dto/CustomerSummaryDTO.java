package com.bookstore.management.customer.dto;

public record CustomerSummaryDTO(
        Long id,
        String name,
        String lastName,
        String email
) {

}
