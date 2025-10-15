package com.bookstore.management.book.service;

import com.bookstore.management.book.dto.CreateBookDTO;
import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.book.repository.AuthorRepository;
import com.bookstore.management.book.repository.BookRepository;
import com.bookstore.management.shared.exception.custom.AuthorNotFoundException;
import com.bookstore.management.shared.exception.custom.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final BookMapper bookMapper;

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
    public Book createBook(CreateBookDTO createBookDto) {

        if(bookRepository.existsBookByIsbn(createBookDto.getIsbn())){
            throw new IllegalArgumentException("Book already exists");
        }

        Book book = bookMapper.toEntity(createBookDto);

        log.info("Created new book with id: {}", book.getId());

        return bookRepository.save(book);
    }
    @Transactional
    public Book updateBook(CreateBookDTO createBookDto, Long id){
        Book existingBook = bookRepository
                .findById(id)
                .orElseThrow(()-> new BookNotFoundException("Book", "Id", id));

        bookMapper.updateEntityFromDto(createBookDto, existingBook);

        log.info("Book updated with id: {}", existingBook.getId());

        return bookRepository.save(existingBook);
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
        Book existingBook = bookRepository
                .findById(bookId)
                .orElseThrow(()-> new BookNotFoundException("Book", "Id", bookId));

        if (existingBook.getAuthor()!= null && newAuthorId.equals(existingBook.getAuthor().getId())) {
            log.info("Book already has the same author with id: {}", newAuthorId);
            return existingBook;
        }
        Author newAuthor = authorRepository
                .findById(newAuthorId)
                        .orElseThrow(()-> new AuthorNotFoundException("Author", "Id", newAuthorId));

        existingBook.setAuthor(newAuthor);

        log.info("Modified author with id: {}", existingBook.getAuthor().getId());

        return bookRepository.save(existingBook);
    }



}
