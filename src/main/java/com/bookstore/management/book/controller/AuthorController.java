package com.bookstore.management.book.controller;

import com.bookstore.management.book.dto.CreateAuthorDTO;
import com.bookstore.management.book.dto.AuthorResponseDTO;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.service.AuthorService;
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
    public ResponseEntity<?> save(@Valid @RequestBody CreateAuthorDTO createAuthorDto) {
        Author author = authorService.createAuthor(createAuthorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthorResponseDTO(author.getId()));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @Positive Long id) {
        authorService.deleteAuthorById(id);
        return ResponseEntity.ok("Author Deleted Successfully");
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody CreateAuthorDTO createAuthorDto, @PathVariable @Positive Long id) {
        Author author = authorService.updateAuthor(createAuthorDto, id);
        return ResponseEntity.ok(new AuthorResponseDTO(author.getId()));

    }

}
