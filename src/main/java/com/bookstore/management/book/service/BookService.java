package com.bookstore.management.book.service;

import com.bookstore.management.book.dto.BookResponseDTO;
import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.book.dto.CreateBookDTO;
import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.book.repository.AuthorRepository;
import com.bookstore.management.book.repository.BookRepository;
import com.bookstore.management.shared.exception.custom.DuplicateEntityException;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final BookMapper bookMapper;

    public List<BookSummaryDTO> findAll() {

        return bookMapper.toBookSummaryDTOList(bookRepository.findAll());
    }
    public BookResponseDTO findById(Long id) {
        return bookMapper.toBookResponseDTO(findByIdOrThrow(id));
    }
    private Book findByIdOrThrow(Long id){
        return bookRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Book","Id",id));
    }
    public BookResponseDTO findByISBN(String isbn) {
        Book book = bookRepository.findBookByIsbn(isbn).
                orElseThrow(()-> new ResourceNotFoundException("Book", "ISBN", isbn));
        return bookMapper.toBookResponseDTO(book);
    }

    public List<BookSummaryDTO> booksByAuthorId(Long authorId) {

        if (!authorRepository.existsById(authorId)){
            throw new ResourceNotFoundException("Author","Id",authorId);
        }
        List<Book> books = bookRepository.findBooksByAuthorId(authorId);
        return bookMapper.toBookSummaryDTOList(books);
    }
    @Transactional
    public BookResponseDTO createBook(CreateBookDTO createBookDto) {
        Optional<Book> bookResponse = bookRepository.findBookByIsbn(createBookDto.getIsbn());
        if (bookResponse.isPresent()) {
            throw new DuplicateEntityException("Book","ISBN",createBookDto.getIsbn());
        }
        Book book = bookMapper.toEntity(createBookDto);
        Author author = authorRepository
                .findById(createBookDto.getAuthorId())
                .orElseThrow(()-> new ResourceNotFoundException("Author","Id",createBookDto.getAuthorId()));
        book.setAuthor(author);

        return bookMapper.toBookResponseDTO(bookRepository.save(book));
    }
    @Transactional
    public BookResponseDTO updateBook(CreateBookDTO createBookDto, Long id){
        Book existingBook = findByIdOrThrow(id);
        bookMapper.updateEntityFromDto(createBookDto, existingBook);
        Author author = authorRepository
                .findById(createBookDto.getAuthorId())
                .orElseThrow(()-> new ResourceNotFoundException("Author","Id",createBookDto.getAuthorId()));
        existingBook.setAuthor(author);
        return  bookMapper.toBookResponseDTO(bookRepository.save(existingBook));
    }
    @Transactional
    public void deleteById(Long id){
        findByIdOrThrow(id);
        bookRepository.deleteById(id);
    }
    @Transactional
    public BookResponseDTO updateAuthor(Long bookId, Long newAuthorId){
        Book existingBook = findByIdOrThrow(bookId);

        if (newAuthorId.equals(existingBook.getAuthor().getId())) {
            return bookMapper.toBookResponseDTO(existingBook);
        }

        Author newAuthor = authorRepository
                .findById(newAuthorId)
                        .orElseThrow(()-> new ResourceNotFoundException("Author", "Id", newAuthorId));

        existingBook.setAuthor(newAuthor);

        return bookMapper.toBookResponseDTO(bookRepository.save(existingBook));
    }
}
