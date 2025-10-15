package com.bookstore.management.book.controller;

import com.bookstore.management.book.dto.CreateBookDTO;
import com.bookstore.management.book.dto.BookResponseDTO;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.book.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> findAll(){
        return ResponseEntity.ok(bookService.findAll());
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }
    @GetMapping(value = "/isbn/{isbn}")
    public ResponseEntity<?> findByIsbn(@PathVariable String isbn){
        return ResponseEntity.ok(bookService.findByISBN(isbn));
    }
    @GetMapping(value = "/author/{authorId}")
    public ResponseEntity<List<Book>> findByAuthorId(@PathVariable @Positive Long authorId){
        return ResponseEntity.ok(bookService.booksByAuthorId(authorId));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @Positive Long id) {
        bookService.deleteById(id);
        return ResponseEntity.ok("Book deleted successfully");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateBookDTO createBookDto){
        Book book  = bookService.createBook(createBookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BookResponseDTO(book.getId()));

    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable @Positive Long id, @RequestBody @Valid CreateBookDTO createBookDto){
        Book book = bookService.updateBook(createBookDto, id);
        return ResponseEntity.ok(new BookResponseDTO(book.getId()));
    }

    @PatchMapping(value = "/{id}/author/{newAuthorId}")
    public ResponseEntity<?> updateAuthor(@PathVariable @Positive Long id, @PathVariable @Positive Long newAuthorId){
        Book book = bookService.updateAuthor(id,newAuthorId);
        return ResponseEntity.ok(new BookResponseDTO(book.getId()));
    }

}
