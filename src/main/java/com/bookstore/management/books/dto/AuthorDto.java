package com.bookstore.management.books.dto;

import com.bookstore.management.books.model.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorDto {

    @NotBlank(message = "The author's name is obligatory")
    @Size(min = 2, max = 100, message = "The name must be between 2 and 100 characters")
    public String name;

    @NotBlank(message = "The nationality is obligatory")
    @Size(min = 2, max = 50, message = "The nationality must be between 2 and 50 characters")
    public String nationality;

    @NotNull(message = "The date of birth is obligatory")
    @Past(message = "Date of birth must be before today")
    public LocalDate dateOfBirth;

    @NotNull(message = "The gender is obligatory")
    public Author.Gender gender;

    @Size(max = 2000, message = "The biography cannot exceed 2000 characters")
    public String biography;

}
