package com.bookstore.management.books.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "The ISBN is obligatory")
    private String ISBN;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "The title is obligatory")
    private String title;

    @Column(name = "publish_date", nullable = false)
    @NotNull(message = "the publish date is obligatory")
    @PastOrPresent(message = "the publish date cannot be in the future")
    private LocalDate publishDate;

    @Column(name = "description", length = 1024)
    @Size(max = 1024, message = "Description cannot exceed 1024 characters")
    private String description;

    @Column(name = "pages", nullable = false)
    @NotNull(message = "The number of pages is obligatory")
    @Min(value = 1,message = "The book must have at least one page")
    @Max(value = 10000, message = "The number of page must not exceed 10,000")
    private Integer pages;

    @Column(name = "genre")
    @Size(max = 50, message = "The genre cannot exceed 50 characters")
    private String genre;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;


    public Book(String ISBN, String title, LocalDate publishDate, String description, Integer pages, Author author) {
        this.ISBN = ISBN;
        this.title = title;
        this.publishDate = publishDate;
        this.description = description;
        this.pages = pages;
        this.author = author;
    }

    public Book(String ISBN, String title, LocalDate publishDate, String description, Integer pages, String genre, Author author) {
        this.ISBN = ISBN;
        this.title = title;
        this.publishDate = publishDate;
        this.description = description;
        this.pages = pages;
        this.genre = genre;
        this.author = author;
    }
}
