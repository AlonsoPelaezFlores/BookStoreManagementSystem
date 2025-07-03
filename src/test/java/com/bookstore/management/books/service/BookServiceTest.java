package com.bookstore.management.books.service;

import com.bookstore.management.books.dto.BookDto;
import com.bookstore.management.books.model.Author;
import com.bookstore.management.books.model.Book;
import com.bookstore.management.books.repository.AuthorRepository;
import com.bookstore.management.books.repository.BookRepository;
import com.bookstore.management.shared.exception.custom.AuthorNotFoundException;
import com.bookstore.management.shared.exception.custom.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    @Nested
    class findAll{
        private Book book;
        private Book book2;
        private Author author;
        @BeforeEach
        void setUp() {
            author = Author.builder()
                    .id(1L)
                    .name("Chimamanda Ngozi Adichie")
                    .nationality("Nigerian")
                    .gender(Author.Gender.FEMALE)
                    .build();

            book = Book.builder()
                    .id(1L)
                    .ISBN("9780007356348")
                    .title("Half of a Yellow Sun")
                    .publishDate(LocalDate.of(2006, 8, 10))
                    .description("A story set during the Nigerian Civil War.")
                    .pages(433)
                    .genre("Historical Fiction")
                    .author(author)
                    .build();

            book2 = Book.builder()
                    .id(2L)
                    .ISBN("9780307455925")
                    .title("Americanah")
                    .publishDate(LocalDate.of(2013, 5, 14))
                    .description("A powerful story of love and race spanning Nigeria and America.")
                    .pages(588)
                    .genre("Contemporary")
                    .author(author)
                    .build();
        }

        @Test
        @DisplayName("Should return all books when books exist in repository")
        void whenBooksExist_ShouldReturnAllBooks(){
            List<Book> expectedBooks = Arrays.asList(book, book2);
            when(bookRepository.findAll()).thenReturn(expectedBooks);

            List<Book> actualBooks = bookService.findAll();

            assertThat(actualBooks).isNotNull();
            assertThat(actualBooks).hasSize(2);
            assertThat(actualBooks).isEqualTo(expectedBooks);


        }
        @Test
        @DisplayName("Should return empty list when no books exist in repository")
        void whenNoBooksExist_ShouldReturnEmptyList(){
            List<Book> expectBooks = Collections.emptyList();
            when(bookRepository.findAll()).thenReturn(expectBooks);

            List<Book> actualBooks = bookService.findAll();

            assertThat(actualBooks).hasSize(0);
        }
    }

    @Nested
    class findById{
        private Book book;
        private Author author;
        @BeforeEach
        void setUp() {
            author= Author.builder()
                    .id(1L)
                    .name("Gabriel García Márquez")
                    .nationality("Colombian")
                    .dateOfBirth(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist, short-story writer, screenwriter, and journalist, known affectionately as Gabo or Gabito throughout Latin America.")
                    .build();
            book = Book.builder()
                    .id(1L)
                    .ISBN("9780007356348")
                    .title("Half of a Yellow Sun")
                    .publishDate(LocalDate.of(2006, 8, 10))
                    .description("A story set during the Nigerian Civil War.")
                    .pages(433)
                    .genre("Historical Fiction")
                    .author(author)
                    .build();
        }
        @Test
        @DisplayName("Should return book when book exist")
        void whenBookExists_ShouldReturnBook(){
            Long bookId = 1L;

            when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

            Book actualBook = bookService.findById(bookId);

            assertThat(actualBook).isNotNull();
            assertThat(actualBook.getId()).isEqualTo(bookId);
            assertThat(actualBook).isEqualTo(book);

            verify(bookRepository).findById(bookId);
        }
        @Test
        @DisplayName("Should throw BookNotFoundException when book is missing")
        void whenBookNotFound_ShouldThrowBookNotFoundException(){

            Long nonExistentId = 888L;
            when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(()-> bookService.findById(nonExistentId))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("888");

            verify(bookRepository).findById(nonExistentId);
        }
    }

    @Nested
    class findByISBN{
        private Book book;
        private Author author;
        @BeforeEach
        void setUp() {
            author = Author.builder()
                    .id(1L)
                    .name("Chimamanda Ngozi Adichie")
                    .nationality("Nigerian")
                    .gender(Author.Gender.FEMALE)
                    .build();

            book = Book.builder()
                    .id(1L)
                    .ISBN("9780007356348")
                    .title("Half of a Yellow Sun")
                    .publishDate(LocalDate.of(2006, 8, 10))
                    .description("A story set during the Nigerian Civil War.")
                    .pages(433)
                    .genre("Historical Fiction")
                    .author(author)
                    .build();

        }
        @Test
        @DisplayName("Should return the book when the book is found by isbn")
        void whenBookExists_ShouldReturnBook(){
            String bookISBN = "9780007356348";
            when(bookRepository.findBookByISBN(bookISBN)).thenReturn(Optional.ofNullable(book));

            Book actualBook = bookService.findByISBN(bookISBN);
            assertThat(actualBook).isEqualTo(book);
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when the book is missing ")
        void whenBookNotFound_ShouldThrowBookNotFoundException(){
            String nonExistentISBN = "1234678990";
            when(bookRepository.findBookByISBN(nonExistentISBN)).thenReturn(Optional.empty());

            assertThatThrownBy(()-> bookService.findByISBN(nonExistentISBN))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("ISBN")
                    .hasMessageContaining("1234678990");
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when the isbn from book is null")
        void whenISBNIsNull_ShouldThrowBookNotFoundException(){
            String nonExistentISBN = null;
            when(bookRepository.findBookByISBN(nonExistentISBN)).thenReturn(Optional.empty());

            assertThatThrownBy(()-> bookService.findByISBN(nonExistentISBN))
            .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("ISBN")
                    .hasMessageContaining("null");

        }
    }

    @Nested
    class createBook{
        private Book expectBook;
        private Author author;
        private BookDto bookDto;
        @BeforeEach
        void setUp() {
            author = Author.builder()
                    .id(1L)
                    .name("Chimamanda Ngozi Adichie")
                    .nationality("Nigerian")
                    .gender(Author.Gender.FEMALE)
                    .build();

            expectBook = Book.builder()
                    .id(1L)
                    .ISBN("9780007356348")
                    .title("Half of a Yellow Sun")
                    .publishDate(LocalDate.of(2006, 8, 10))
                    .description("A story set during the Nigerian Civil War.")
                    .pages(433)
                    .genre("Historical Fiction")
                    .author(author)
                    .build();

            bookDto = BookDto.builder()
                    .ISBN("9780007356348")
                    .title("Half of a Yellow Sun")
                    .publishDate(LocalDate.of(2006, 8, 10))
                    .description("A story set during the Nigerian Civil War.")
                    .pages(433)
                    .genre("Historical Fiction")
                    .author(author)
                    .build();

        }

        @Test
        @DisplayName("Should return book saved when creating book with valid data")
        void withValidData_ShouldReturnSavedBook(){
            when(bookRepository.save(any(Book.class))).thenReturn(expectBook);

            Book actualBook = bookService.createBook(bookDto);

            assertThat(actualBook).isNotNull();
            assertThat(actualBook.getId()).isEqualTo(expectBook.getId());
            assertThat(actualBook.getISBN()).isEqualTo(expectBook.getISBN());
            assertThat(actualBook.getTitle()).isEqualTo(expectBook.getTitle());
            assertThat(actualBook.getPublishDate()).isEqualTo(expectBook.getPublishDate());
            assertThat(actualBook.getDescription()).isEqualTo(expectBook.getDescription());
            assertThat(actualBook.getPages()).isEqualTo(expectBook.getPages());
            assertThat(actualBook.getGenre()).isEqualTo(expectBook.getGenre());
            assertThat(actualBook.getAuthor()).isEqualTo(expectBook.getAuthor());


        }
        @Test
        @DisplayName("Should call repository save when creating book with valid data")
        void withValidData_ShouldCallRepositorySave(){

            when(bookRepository.save(any(Book.class))).thenReturn(expectBook);

            bookService.createBook(bookDto);

            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

            verify(bookRepository).save(bookCaptor.capture());

            Book capturedBook = bookCaptor.getValue();

            assertThat(capturedBook.getISBN()).isEqualTo(expectBook.getISBN());
            assertThat(capturedBook.getTitle()).isEqualTo(expectBook.getTitle());
            assertThat(capturedBook.getPublishDate()).isEqualTo(expectBook.getPublishDate());
            assertThat(capturedBook.getDescription()).isEqualTo(expectBook.getDescription());
            assertThat(capturedBook.getPages()).isEqualTo(expectBook.getPages());
            assertThat(capturedBook.getGenre()).isEqualTo(expectBook.getGenre());
            assertThat(capturedBook.getAuthor()).isEqualTo(expectBook.getAuthor());

            assertThat(capturedBook.getId()).isNull();
        }
    }
    @Nested
    class updateBook{
        private Book expectBook;
        private Author author;
        private BookDto bookDto;
        @BeforeEach
        void setUp() {
            author = Author.builder()
                    .name("Haruki Murakami")
                    .nationality("Japanese")
                    .gender(Author.Gender.MALE)
                    .build();
            bookDto =  BookDto.builder()
                    .ISBN("9780099448822")
                    .title("Kafka on the Shore")
                    .publishDate(LocalDate.of(2002, 9, 12))
                    .description("A surreal journey of a teenage boy and a man who talks to cats.")
                    .pages(505)
                    .genre("Magical Realism")
                    .author(author)
                    .build();
            expectBook = Book.builder()
                    .id(1L)
                    .ISBN("9780099448822")
                    .title("Kafka on the Shore")
                    .publishDate(LocalDate.of(2002, 9, 12))
                    .description("A surreal journey of a teenage boy and a man who talks to cats.")
                    .pages(505)
                    .genre("Magical Realism")
                    .author(author)
                    .build();

        }
        @Test
        @DisplayName("Should return update book when book exists")
        void whenBookExists_ShouldReturnUpdatedBook(){
            Long bookId = 1L;
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectBook));
            when(bookRepository.save(any(Book.class))).thenReturn(expectBook);

            Book actualBook = bookService.updateBook(bookDto, bookId);

            assertThat(actualBook).isNotNull();

            verify(bookRepository).findById(bookId);
            verify(bookRepository).save(any(Book.class));
        }
        @Test
        @DisplayName("Should throw BookNotFoundException book is not found on update")
        void whenBookNotFound_ShouldThrowBookNotFoundException(){
            Long nonExistingBookId = 999L;

            when(bookRepository.findById(nonExistingBookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.updateBook(bookDto, nonExistingBookId))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

        }
        @Test
        @DisplayName("Should update all fields with valid data")
        void withValidData_ShouldUpdateAllFields(){
            Long bookId = 1L;
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectBook));
            when(bookRepository.save(any(Book.class))).thenReturn(expectBook);

            Book actualBook = bookService.updateBook(bookDto, bookId);

            assertThat(actualBook.getISBN()).isEqualTo(expectBook.getISBN());
            assertThat(actualBook.getTitle()).isEqualTo(expectBook.getTitle());
            assertThat(actualBook.getPublishDate()).isEqualTo(expectBook.getPublishDate());
            assertThat(actualBook.getDescription()).isEqualTo(expectBook.getDescription());
            assertThat(actualBook.getPages()).isEqualTo(expectBook.getPages());
            assertThat(actualBook.getGenre()).isEqualTo(expectBook.getGenre());
            assertThat(actualBook.getAuthor()).isEqualTo(expectBook.getAuthor());

        }
        @Test
        @DisplayName("Should call repository save")
        void shouldCallRepositorySave(){
            Long bookId = 1L;

            when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectBook));
            when(bookRepository.save(any(Book.class))).thenReturn(expectBook);

            bookService.updateBook(bookDto, bookId);

            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

            verify(bookRepository).save(bookCaptor.capture());

            Book captureBook = bookCaptor.getValue();

            assertThat(captureBook.getISBN()).isEqualTo(expectBook.getISBN());
            assertThat(captureBook.getTitle()).isEqualTo(expectBook.getTitle());
            assertThat(captureBook.getPublishDate()).isEqualTo(expectBook.getPublishDate());
            assertThat(captureBook.getDescription()).isEqualTo(expectBook.getDescription());
            assertThat(captureBook.getPages()).isEqualTo(expectBook.getPages());
            assertThat(captureBook.getGenre()).isEqualTo(expectBook.getGenre());
            assertThat(captureBook.getAuthor()).isEqualTo(expectBook.getAuthor());
        }
    }
    @Nested
    class deleteBookById{

        @Test
        @DisplayName("Should delete successfully when the book exists")
        void whenBookExists_ShouldDeleteSuccessfully(){
            Long bookId = 1L;

            when(bookRepository.existsById(bookId)).thenReturn(true);

            bookService.deleteById(bookId);

            verify(bookRepository).existsById(bookId);
            verify(bookRepository).deleteById(bookId);
        }
        @Test
        @DisplayName("Should throw BookNotFoundException when book is missing")
        void whenBookNotFound_ShouldThrowBookNotFoundException(){
            Long nonExistingBookId = 999L;

            when(bookRepository.existsById(nonExistingBookId)).thenReturn(false);

            assertThatThrownBy(() -> bookService.deleteById(nonExistingBookId))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

            verify(bookRepository).existsById(nonExistingBookId);
            verify(bookRepository, never()).deleteById(any(Long.class));
        }
        @Test
        @DisplayName("Should call repository delete by id")
        void shouldCallRepositoryDeleteById(){
            Long bookId = 1L;

            when(bookRepository.existsById(bookId)).thenReturn(true);

            bookService.deleteById(bookId);

            verify(bookRepository).deleteById(bookId);
        }
    }

    @Nested
    class updateAuthor{
        private Author author;
        private Author newAuthor;
        private Book book;
        private Book expectBook;

        @BeforeEach
        void setUp(){
            author = Author.builder()
                    .id(1L)
                    .name("Alex Jordan")
                    .nationality("Canadian")
                    .gender(Author.Gender.PREFER_NOT_TO_SAY)
                    .build();

            newAuthor = Author.builder()
                    .id(2L)
                    .name("Haruki Murakami")
                    .nationality("Japanese")
                    .gender(Author.Gender.MALE)
                    .build();

            book = Book.builder()
                    .id(1L)
                    .ISBN("9789876543210")
                    .title("Silent Variations")
                    .publishDate(LocalDate.of(2019, 3, 12))
                    .description("A collection of introspective short stories.")
                    .pages(180)
                    .genre("Literary Fiction")
                    .author(author)
                    .build();
            expectBook = Book.builder()
                    .id(1L)
                    .ISBN("9789876543210")
                    .title("Silent Variations")
                    .publishDate(LocalDate.of(2019, 3, 12))
                    .description("A collection of introspective short stories.")
                    .pages(180)
                    .genre("Literary Fiction")
                    .author(newAuthor)
                    .build();

        }
        @Test
        @DisplayName("Should return update book author when author and book exist")
        void WhenBookAndAuthorExist_ShouldReturnUpdateBookAuthor(){

            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
            when(bookRepository.save(any(Book.class))).thenReturn(expectBook);

            Book bookUpdated = bookService.updateAuthor(1L, 2L);

            assertThat(bookUpdated).isNotNull();
            assertThat(bookUpdated.getAuthor()).isEqualTo(newAuthor);
            assertThat(bookUpdated.getAuthor().getId()).isEqualTo(2L);
            assertThat(bookUpdated.getAuthor().getName()).isEqualTo("Haruki Murakami");
            assertThat(bookUpdated.getId()).isEqualTo(1L);

            verify(bookRepository).findById(1L);
            verify(authorRepository).findById(2L);
            verify(bookRepository).save(any(Book.class));

        }
        @Test
        @DisplayName("Should not call repository and return unchanged when book already has same author id")
        void WhenBookAlreadyHasSameAuthor_ShouldNotCallRepositoryAndReturnUnchanged(){

            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

            Book bookUpdated = bookService.updateAuthor(1L, 1L);

            assertThat(bookUpdated).isNotNull();
            assertThat(bookUpdated.getAuthor()).isEqualTo(author);
            assertThat(bookUpdated.getAuthor().getId()).isEqualTo(1L);
            assertThat(bookUpdated).isSameAs(book);

            verify(bookRepository).findById(1L);
            verify(authorRepository, never()).findById(any(Long.class));
            verify(bookRepository, never()).save(any(Book.class));
        }
        @Test
        @DisplayName("Should throw BookNotFoundException when book is missing")
        void WhenBookNotFound_ShouldThrowBookNotFoundException(){
            Long nonExistingBookId = 999L;

            when(bookRepository.findById(nonExistingBookId)).thenReturn(Optional.empty());

            assertThatThrownBy(()-> bookService.updateAuthor(nonExistingBookId,2L))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("Book")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");
            verify(bookRepository).findById(nonExistingBookId);
            verify(bookRepository, never()).save(any(Book.class));
        }
        @Test
        @DisplayName("Should throw AuthorNotFoundException when author is missing")
        void WhenAuthorNotFound_ShouldThrowAuthorNotFoundException(){
            Long nonExistingAuthorId = 999L;
            Long bookId = 1L;

            when(authorRepository.findById(nonExistingAuthorId)).thenReturn(Optional.empty());
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

            assertThatThrownBy(()-> bookService.updateAuthor(bookId,nonExistingAuthorId))
                    .isInstanceOf(AuthorNotFoundException.class)
                    .hasMessageContaining("Author")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

            verify(bookRepository).findById(bookId);
            verify(authorRepository).findById(nonExistingAuthorId);
            verify(bookRepository, never()).save(any(Book.class));
        }
    }

}