package com.bookstore.management.book.dto;

import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.validation.ValidIsbn;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
public class CreateBookDTO {

        @NotBlank(message = "ISBN cannot be blank")
        @ValidIsbn(message = "Isbn must be valid (ISBN-10 or ISBN-13)")
        private String isbn;

        @NotBlank(message = "Title cannot be blank")
        private String title;

        @NotNull(message = "Publish date cannot be null")
        @PastOrPresent(message = "the publish date cannot be in the future")
        private LocalDate publishDate;

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 1024, message = "Description cannot exceed 1024 characters")
        private String description;

        @NotNull(message = "Pages cannot be null")
        @Positive(message = "Pages must be positive")
        @Max(value = 10000, message = "The number of page must not exceed 10,000")
        private Integer pages;

        @NotBlank(message = "Genre cannot be blank")
        @Size(max = 50, message = "The genre cannot exceed 50 characters")
        private String genre;

        @NotNull(message = "author cannot be null")
        private Author author;
    }
