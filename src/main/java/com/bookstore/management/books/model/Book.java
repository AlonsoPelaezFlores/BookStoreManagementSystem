package com.bookstore.management.books.model;

import com.bookstore.management.books.validation.ValidIsbn;
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

    @Column(name = "isbn", unique = true, nullable = false)
    @ValidIsbn
    private String isbn;

    @Column(name = "title", nullable = false)
    @NotBlank
    private String title;

    @Column(name = "publish_date", nullable = false)
    @NotNull
    @PastOrPresent
    private LocalDate publishDate;

    @Column(name = "description", length = 1024)
    @Size(max = 1024)
    private String description;

    @Column(name = "pages", nullable = false)
    @NotNull
    @Positive
    @Max(value = 10000)
    private Integer pages;

    @Column(name = "genre")
    @Size(max = 50)
    private String genre;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;


    public Book(String isbn, String title, LocalDate publishDate, String description, Integer pages, Author author) {
        this.isbn = isbn;
        this.title = title;
        this.publishDate = publishDate;
        this.description = description;
        this.pages = pages;
        this.author = author;
    }

    public Book(String isbn, String title, LocalDate publishDate, String description, Integer pages, String genre, Author author) {
        this.isbn = isbn;
        this.title = title;
        this.publishDate = publishDate;
        this.description = description;
        this.pages = pages;
        this.genre = genre;
        this.author = author;
    }
}
