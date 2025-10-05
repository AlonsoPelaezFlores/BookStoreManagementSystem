package com.bookstore.management.book.model;

import jakarta.persistence.*;
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
    private String isbn;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "pages", nullable = false)
    private Integer pages;

    @Column(name = "genre", length = 100)
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
