package com.bookstore.management.books.controller;

import com.bookstore.management.books.dto.AuthorDto;
import com.bookstore.management.books.dto.AuthorResponse;
import com.bookstore.management.books.model.Author;
import com.bookstore.management.books.service.AuthorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/authors")
public class AuthorController {


    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<Author>> findAll() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid AuthorDto authorDto) {
        Author author = authorService.createAuthor(authorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthorResponse(author.getName()));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @Positive Long id) {
        authorService.deleteAuthorById(id);
        return ResponseEntity.ok("Author Deleted Successfully");
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid AuthorDto authorDto, @PathVariable @Positive Long id) {
        Author author = authorService.updateAuthor(authorDto, id);
        return ResponseEntity.ok(new AuthorResponse(author.getName()));

    }

}
