package com.bookstore.management.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Email is obligatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is obligatory")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
}
