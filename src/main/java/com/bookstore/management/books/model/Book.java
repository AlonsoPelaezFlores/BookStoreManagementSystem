package com.bookstore.management.books.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String ISBN;
    private String title;
    private LocalDate publishDate;
    private String description;
    private Integer pages;
    @ManyToOne
    private Author author;
    private String imageUrl;
}
