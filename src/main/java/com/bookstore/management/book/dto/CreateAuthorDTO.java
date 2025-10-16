package com.bookstore.management.book.dto;

import com.bookstore.management.book.model.Author;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAuthorDTO {

    @NotBlank(message = "The author's name is obligatory")
    @Size(min = 2, max = 100, message = "The name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "The nationality is obligatory")
    @Size(min = 2, max = 50, message = "The nationality must be between 2 and 50 characters")
    private String nationality;

    @NotNull(message = "The date of birth is obligatory")
    @Past(message = "Date of birth must be before today")
    private LocalDate birthDate;

    @NotNull(message = "The gender is obligatory")
    private Author.Gender gender;

    @Size(max = 2000, message = "The biography cannot exceed 2000 characters")
    private String biography;

}
