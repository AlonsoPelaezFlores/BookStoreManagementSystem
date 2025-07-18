package com.bookstore.management.books.dto;

import com.bookstore.management.books.model.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class AuthorDto {
    public String name;
    public String nationality;
    public LocalDate dateOfBirth;
    public Author.Gender gender;
    public String biography;

}
