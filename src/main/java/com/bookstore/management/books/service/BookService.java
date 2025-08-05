package com.bookstore.management.books.service;

import com.bookstore.management.books.dto.BookDto;
import com.bookstore.management.books.model.Author;
import com.bookstore.management.books.model.Book;
import com.bookstore.management.books.repository.AuthorRepository;
import com.bookstore.management.books.repository.BookRepository;
import com.bookstore.management.shared.exception.custom.AuthorNotFoundException;
import com.bookstore.management.shared.exception.custom.BookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(()-> new BookNotFoundException("Book","Id",id));
    }
    public Book findByISBN(String isbn) {
        return bookRepository.findBookByIsbn(isbn).
                orElseThrow(()-> new BookNotFoundException("Book", "ISBN", isbn));
    }
    public List<Book> booksByAuthorId(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new AuthorNotFoundException("Author","Id",authorId);
        }
        return bookRepository.findBooksByAuthorId(authorId);
    }
    @Transactional
    public Book createBook(BookDto bookDto) {

        Book book = Book.builder()
                .isbn(bookDto.getIsbn())
                .title(bookDto.getTitle())
                .publishDate(bookDto.getPublishDate())
                .description(bookDto.getDescription())
                .pages(bookDto.getPages())
                .genre(bookDto.getGenre())
                .author(bookDto.getAuthor())
                .build();
        log.info("Created new book with id: {}", book.getId());

        return bookRepository.save(book);
    }
    @Transactional
    public Book updateBook(BookDto bookDto, Long id){
        Book bookToModified = bookRepository
                .findById(id)
                .orElseThrow(()-> new BookNotFoundException("Book", "Id", id));

        bookToModified.setTitle(bookDto.getTitle());
        bookToModified.setPublishDate(bookDto.getPublishDate());
        bookToModified.setDescription(bookDto.getDescription());
        bookToModified.setPages(bookDto.getPages());
        bookToModified.setGenre(bookDto.getGenre());
        bookToModified.setAuthor(bookDto.getAuthor());

        log.info("Book updated with id: {}", bookToModified.getId());

        return bookRepository.save(bookToModified);
    }
    @Transactional
    public void deleteById(Long id){
        if(!bookRepository.existsById(id)){
            throw new BookNotFoundException("Book", "Id", id);
        }
        log.info("Book deleted with id: {}", id);
        bookRepository.deleteById(id);
    }
    @Transactional
    public Book updateAuthor(Long bookId, Long newAuthorId){
        Book bookModified = bookRepository
                .findById(bookId)
                .orElseThrow(()-> new BookNotFoundException("Book", "Id", bookId));

        if (bookModified.getAuthor()!= null && newAuthorId.equals(bookModified.getAuthor().getId())) {
            log.info("Book already has the same author with id: {}", newAuthorId);
            return bookModified;
        }
        Author newAuthor = authorRepository
                .findById(newAuthorId)
                        .orElseThrow(()-> new AuthorNotFoundException("Author", "Id", newAuthorId));

        bookModified.setAuthor(newAuthor);

        log.info("Modified author with id: {}", bookModified.getAuthor().getId());

        return bookRepository.save(bookModified);
    }



}
