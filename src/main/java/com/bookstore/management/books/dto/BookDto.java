package com.bookstore.management.books.dto;

import com.bookstore.management.books.model.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
public class BookDto {

    private String ISBN;
    private String title;
    private LocalDate publishDate;
    private String description;
    private Integer pages;
    private String genre;
    private Author author;
}
