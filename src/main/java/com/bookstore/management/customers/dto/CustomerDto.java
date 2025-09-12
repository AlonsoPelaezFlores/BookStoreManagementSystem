package com.bookstore.management.customers.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto{

    @NotBlank(message = "The name is obligatory")
    @Size(min=2, max = 100, message = "The name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "The surname is obligatory")
    @Size(min=2, max = 100, message = "The surname must be between 2 and 100 characters")
    private String surname;

    @Email(message = "The email must be valid")
    @NotBlank(message = "The email is obligatory")
    @Size(max = 150, message = "The email must not exceed 150 characters")
    private String email;

    @NotNull(message = "The birth date is obligatory")
    @Past(message = "Date of birth must be before today")
    private LocalDate birthDate;
}
