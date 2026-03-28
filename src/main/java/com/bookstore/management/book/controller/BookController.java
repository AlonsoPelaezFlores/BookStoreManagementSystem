package com.bookstore.management.book.controller;

import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.book.dto.CreateBookDTO;
import com.bookstore.management.book.dto.BookResponseDTO;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.book.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Books", description = "Book management")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookSummaryDTO>> findAll(){
        return ResponseEntity.ok(bookService.findAll());
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
