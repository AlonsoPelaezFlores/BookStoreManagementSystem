package com.bookstore.management.customer.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerCreateDTO {

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
