package com.bookstore.management.book.controller;

import com.bookstore.management.book.dto.BookResponseDTO;
import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.book.dto.CreateBookDTO;
import com.bookstore.management.book.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Books", description = "Book management")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    private final static int DEFAULT_SIZE = 10;
    private final static String DEFAULT_SORT = "id";

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookSummaryDTO>> findAll(
            @PageableDefault(size = DEFAULT_SIZE, sort = DEFAULT_SORT) Pageable pageable){
        return ResponseEntity.ok(bookService.findAll(pageable));
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<BookResponseDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }
    @GetMapping(value = "/isbn/{isbn}")
    public ResponseEntity<BookResponseDTO> findByIsbn(@PathVariable String isbn){
        return ResponseEntity.ok(bookService.findByISBN(isbn));
    }
    @GetMapping(value = "/author/{authorId}")
    public ResponseEntity<List<BookSummaryDTO>> findByAuthorId(@PathVariable @Positive Long authorId){
        return ResponseEntity.ok(bookService.booksByAuthorId(authorId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @Positive Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> create(@RequestBody @Valid CreateBookDTO createBookDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(createBookDto));

    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<BookResponseDTO> update(@PathVariable @Positive Long id, @RequestBody @Valid CreateBookDTO createBookDto){
        return ResponseEntity.ok(bookService.updateBook(createBookDto, id));
    }

    @PatchMapping(value = "/{id}/author/{newAuthorId}")
    public ResponseEntity<BookResponseDTO> updateAuthor(@PathVariable @Positive Long id, @PathVariable @Positive Long newAuthorId){
        return ResponseEntity.ok(bookService.updateAuthor(id,newAuthorId));
    }

}
