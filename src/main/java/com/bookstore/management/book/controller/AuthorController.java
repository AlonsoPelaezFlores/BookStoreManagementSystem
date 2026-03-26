package com.bookstore.management.book.controller;

import com.bookstore.management.book.dto.AuthorSummaryDTO;
import com.bookstore.management.book.dto.CreateAuthorDTO;
import com.bookstore.management.book.dto.AuthorResponseDTO;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.service.AuthorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorSummaryDTO>> findAll() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AuthorResponseDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorResponseDTO> save(@Valid @RequestBody CreateAuthorDTO createAuthorDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(createAuthorDto));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @Positive Long id) {
        authorService.deleteAuthorById(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> update(@Valid @RequestBody CreateAuthorDTO createAuthorDto, @PathVariable @Positive Long id) {
        return ResponseEntity.ok(authorService.updateAuthor(createAuthorDto, id));

    }

}
