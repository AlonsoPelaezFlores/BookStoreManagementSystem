package com.bookstore.management.books.controller;

import com.bookstore.management.books.dto.BookDto;
import com.bookstore.management.books.model.Author;
import com.bookstore.management.books.model.Book;
import com.bookstore.management.books.service.BookService;
import com.bookstore.management.shared.exception.custom.AuthorNotFoundException;
import com.bookstore.management.shared.exception.custom.BookNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DisplayName("Book Controller Test")
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BookService bookService;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/books - Find All Books")
    class FindAllBooks {
        private Author author;
        private Book book;
        private Book book2;

        @BeforeEach
        void setUp() {
            author = Author.builder()
                    .id(1L)
                    .name("John Doe")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();
            book = Book.builder()
                    .id(1L)
                    .isbn("978-84-376-0494-7")
                    .title("Cien años de soledad")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();
            book2 = Book.builder()
                    .id(2L)
                    .isbn("978-84-663-1599-6")
                    .title("El nombre del viento")
                    .publishDate(LocalDate.of(2007, 3, 27))
                    .description("Primera entrega de la saga Crónica del Asesino de Reyes, que narra las aventuras de Kvothe, un legendario héroe convertido en posadero.")
                    .pages(662)
                    .genre("Fantasía épica")
                    .author(author)
                    .build();
        }

        @Test
        @DisplayName("Should return all books when books exist")
        void shouldReturnAllBooksWhenBooksExist() throws Exception {
            List<Book> books = Arrays.asList(book, book2);

            when(bookService.findAll()).thenReturn(books);

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].isbn").value("978-84-376-0494-7"))
                    .andExpect(jsonPath("$[0].title").value("Cien años de soledad"))
                    .andExpect(jsonPath("$[0].publishDate").value("1967-06-05"))
                    .andExpect(jsonPath("$[0].pages").value(417))
                    .andExpect(jsonPath("$[0].genre").value("Realismo mágico"))
                    .andExpect(jsonPath("$[0].author.name").value("John Doe"))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].isbn").value("978-84-663-1599-6"))
                    .andExpect(jsonPath("$[1].title").value("El nombre del viento"))
                    .andExpect(jsonPath("$[1].publishDate").value("2007-03-27"))
                    .andExpect(jsonPath("$[1].pages").value(662))
                    .andExpect(jsonPath("$[1].genre").value("Fantasía épica"))
                    .andExpect(jsonPath("$[1].author.name").value("John Doe"));

        }

        @Test
        @DisplayName("Should return empty list when no books exist")
        void shouldReturnEmptyListWhenNoBooksExist() throws Exception {
            when(bookService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
    @Nested
    @DisplayName("GET /api/books/{id} - Find Book By ID")
    class FindBookById {

        @Test
        @DisplayName("Should return book when valid ID is provided")
        void shouldReturnBookWhenValidIdIsProvided() throws Exception {
            Long bookId = 1L;
            Author author = Author.builder()
                    .id(1L)
                    .name("John Doe")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            Book book = Book.builder()
                    .id(bookId)
                    .isbn("978-84-322-1755-4")
                    .title("El hobbit")
                    .publishDate(LocalDate.of(1937, 9, 21))
                    .description("Las aventuras de Bilbo Bolsón, un hobbit que se ve arrastrado a una búsqueda épica para recuperar el reino enano de Erebor.")
                    .pages(310)
                    .genre("Fantasía")
                    .author(author)
                    .build();
            when(bookService.findById(bookId)).thenReturn(book);

            mockMvc.perform(get("/api/books/{id}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(bookId))
                    .andExpect(jsonPath("$.isbn").value("978-84-322-1755-4"))
                    .andExpect(jsonPath("$.title").value("El hobbit"))
                    .andExpect(jsonPath("$.publishDate").value("1937-09-21"))
                    .andExpect(jsonPath("$.description").value("Las aventuras de Bilbo Bolsón, un hobbit que se ve arrastrado a una búsqueda épica para recuperar el reino enano de Erebor."))
                    .andExpect(jsonPath("$.pages").value(310))
                    .andExpect(jsonPath("$.genre").value("Fantasía"))
                    .andExpect(jsonPath("$.author.id").value(1L));
        }
        @Test
        @DisplayName("Should return 404 when book with ID does not exist")
        void shouldReturn404WhenBookWithIdDoesNotExist() throws Exception {
            Long nonExistentId = 1L;

            when(bookService.findById(nonExistentId))
                    .thenThrow(BookNotFoundException.class);

            mockMvc.perform(get("/api/books/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }
        @Test
        @DisplayName("Should return 400 when ID is zero or negative")
        void shouldReturn400WhenIdIsZeroOrNegative() throws Exception {

            mockMvc.perform(get("/api/books/{id}", 0))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(get("/api/books/{id}", -1))
                    .andExpect(status().isBadRequest());
        }

    }
    @Nested
    @DisplayName("GET /api/books/isbn/{isbn} - Find Book By ISBN")
    class FindBookByIsbn {

        @Test
        @DisplayName("Should return book when valid ISBN is provided")
        void shouldReturnBookWhenValidIsbnIsProvided() throws Exception {
            Author author = Author.builder()
                    .id(1L)
                    .name("John Doe")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            String isbn = "978-84-322-1755-4";
            Book book = Book.builder()
                    .isbn(isbn)
                    .title("Cien años de soledad")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia " +
                            "de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();

            when(bookService.findByISBN(isbn)).thenReturn(book);

            mockMvc.perform(get("/api/books/isbn/{isbn}", isbn))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.isbn").value("978-84-322-1755-4"))
                    .andExpect(jsonPath("$.title").value("Cien años de soledad"))
                    .andExpect(jsonPath("$.publishDate").value("1967-06-05"))
                    .andExpect(jsonPath("$.description").value("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo."))
                    .andExpect(jsonPath("$.pages").value(417))
                    .andExpect(jsonPath("$.genre").value("Realismo mágico"))
                    .andExpect(jsonPath("$.author.id").value(1L))
                    .andExpect(jsonPath("$.author.name").value("John Doe"));
        }
        @Test
        @DisplayName("Should return 404 when book with ISBN does not exist")
        void shouldReturn404WhenBookWithIsbnDoesNotExist() throws Exception {
            String nonExistingIsbn = "978-84-322-1755-4";

            when(bookService.findByISBN(nonExistingIsbn)).thenThrow(BookNotFoundException.class);

            mockMvc.perform(get("/api/books/isbn/{isbn}", nonExistingIsbn))
                    .andExpect(status().isNotFound());
        }
    }
    @Nested
    @DisplayName("GET /api/books/author/{authorId} - Find Books By Author")
    class FindBooksByAuthor {

        @Test
        @DisplayName("Should return books when author has books")
        void shouldReturnBooksWhenAuthorHasBooks() throws Exception {
            Long authorId= 1L;

            Author author = Author.builder()
                    .id(authorId)
                    .name("John Doe")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            Book book = Book.builder()
                    .id(1L)
                    .isbn("9780141439518")
                    .title("Cien años de soledad")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();

            Book book2 = Book.builder()
                    .id(2L)
                    .isbn("0756404746")
                    .title("El nombre del viento")
                    .publishDate(LocalDate.of(2007, 3, 27))
                    .description("Primera entrega de la saga Crónica del Asesino de Reyes, que narra las aventuras de Kvothe, un legendario héroe convertido en posadero.")
                    .pages(662)
                    .genre("Fantasía épica")
                    .author(author)
                    .build();

            List<Book> books = Arrays.asList(book, book2);

            when(bookService.booksByAuthorId(authorId)).thenReturn(books);

            mockMvc.perform(get("/api/books/author/{authorId}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].isbn").value("9780141439518"))
                    .andExpect(jsonPath("$[0].title").value("Cien años de soledad"))
                    .andExpect(jsonPath("$[0].publishDate").value("1967-06-05"))
                    .andExpect(jsonPath("$[0].description").value("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo."))
                    .andExpect(jsonPath("$[0].pages").value(417))
                    .andExpect(jsonPath("$[0].genre").value("Realismo mágico"))
                    .andExpect(jsonPath("$[0].author.id").value(1L))

                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].isbn").value("0756404746"))
                    .andExpect(jsonPath("$[1].title").value("El nombre del viento"))
                    .andExpect(jsonPath("$[1].publishDate").value("2007-03-27"))
                    .andExpect(jsonPath("$[1].description").value("Primera entrega de la saga Crónica del Asesino de Reyes, que narra las aventuras de Kvothe, un legendario héroe convertido en posadero."))
                    .andExpect(jsonPath("$[1].pages").value(662))
                    .andExpect(jsonPath("$[1].genre").value("Fantasía épica"))
                    .andExpect(jsonPath("$[1].author.id").value(1L));

        }
        @Test
        @DisplayName("Should return empty list when author has no books")
        void shouldReturnEmptyListWhenAuthorHasNoBooks() throws Exception {
            Long authorId= 1L;

            when(bookService.booksByAuthorId(authorId)).thenReturn(List.of());

            mockMvc.perform(get("/api/books/author/{authorId}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
        @Test
        @DisplayName("Should return 400 when author ID is zero or negative")
        void shouldReturn400WhenAuthorIdIsZeroOrNegative() throws Exception {

            mockMvc.perform(get("/api/books/author/{authorId}", 0))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(get("/api/books/author/{authorId}", -1))
                    .andExpect(status().isBadRequest());
        }

    }
    @Nested
    @DisplayName("POST /api/books - Create Book")
    class CreateBook {
        private BookDto bookDto;
        private Author author;
        private Book book;
        @BeforeEach
        void setUp(){
            author = Author.builder()
                    .id(1L)
                    .name("John Doe")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            bookDto = BookDto.builder()
                    .isbn("9780141439518")
                    .title("Cien años de soledad")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();
            book = Book.builder()
                    .id(1L)
                    .isbn("9780141439518")
                    .title("Cien años de soledad")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();

        }

        @Test
        @DisplayName("Should create book when valid data is provided")
        void shouldCreateBookWhenValidDataIsProvided() throws Exception {

            when(bookService.createBook(any(BookDto.class))).thenReturn(book);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L));
        }
        @Test
        @DisplayName("Should return 400 when invalid data is provided")
        void shouldReturn400WhenInvalidDataIsProvided() throws Exception {

            bookDto = BookDto.builder().build();

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when title is missing")
        void shouldReturn400WhenTitleIsMissing() throws Exception {

            bookDto.setTitle(null);

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 400 when ISBN is missing")
        void shouldReturn400WhenIsbnIsMissing() throws Exception {

            bookDto.setIsbn(null);

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 400 when author is missing")
        void shouldReturn400WhenAuthorIsMissing() throws Exception {

            bookDto.setAuthor(null);

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 400 when pages exceeds maximum")
        void shouldReturn400WhenPagesExceedsMaximum() throws Exception {

            bookDto.setPages(15000);

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 400 when publish date is in future")
        void shouldReturn400WhenPublishDateIsInFuture() throws Exception {

            bookDto.setPublishDate(LocalDate.now().plusDays(1));

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 400 when description exceeds maximum length")
        void shouldReturn400WhenDescriptionExceedsMaximumLength() throws Exception {

            bookDto.setDescription("x".repeat(1025));

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("PUT /api/books/{id} - Update Book")
    class UpdateBook {
        private BookDto bookDto;
        private Book updatedBook;
        private Author author;
        private static final Long bookId = 1L;
        @BeforeEach
        void setUp(){
            author = Author.builder()
                    .id(1L)
                    .name("John Doe")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            bookDto = BookDto.builder()
                    .isbn("9780141439518")
                    .title("Cien años de soledad")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();
            updatedBook = Book.builder()
                    .id(bookId)
                    .isbn("9780141439518")
                    .title("Updated Book")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();
        }

        @Test
        @DisplayName("Should update book when valid data is provided")
        void shouldUpdateBookWhenValidDataIsProvided() throws Exception {

            when(bookService.updateBook(any(BookDto.class), eq(bookId))).thenReturn(updatedBook);

            mockMvc.perform(put("/api/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(bookId));
        }
        @Test
        @DisplayName("Should return 400 when invalid data is provided")
        void shouldReturn400WhenInvalidDataIsProvided() throws Exception{

            bookDto = BookDto.builder().build();

            mockMvc.perform(put("/api/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 400 when ID is zero or negative")
        void shouldReturn400WhenIdIsZeroOrNegative() throws Exception {

            mockMvc.perform(put("/api/books/{id}", 0)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(put("/api/books/{id}", -1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookDto)))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("PATCH /api/books/{id}/author/{newAuthorId} - Update Book Author")
    class UpdateBookAuthor {
        private Author author;
        private Book updatedBook;
        @BeforeEach
        void setUp(){

            author = Author.builder()
                    .id(1L)
                    .name("new author")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.FEMALE)
                    .biography("new famous novelist")
                    .build();

            updatedBook = Book.builder()
                    .id(1L)
                    .isbn("9780141439518")
                    .title("Cien años de soledad")
                    .publishDate(LocalDate.of(1967, 6, 5))
                    .description("Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                    .pages(417)
                    .genre("Realismo mágico")
                    .author(author)
                    .build();
        }

        @Test
        @DisplayName("Should update book author when valid IDs are provided")
        void shouldUpdateBookAuthorWhenValidIdsAreProvided() throws Exception {

            Long bookId = 1L;
            Long newAuthorId = 2L;

            updatedBook.setId(bookId);
            author.setId(newAuthorId);

            when(bookService.updateAuthor(bookId, newAuthorId)).thenReturn(updatedBook);

            mockMvc.perform(patch("/api/books/{id}/author/{newAuthorId}", bookId, newAuthorId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(bookId));

        }

        @Test
        @DisplayName("Should return 404 when book does not exist")
        void shouldReturn404WhenBookDoesNotExist() throws Exception {
            Long nonExistingBookId = 999L;
            Long newAuthorId=2L;

            author.setId(newAuthorId);
            updatedBook.setId(nonExistingBookId);

            when(bookService.updateAuthor(nonExistingBookId,newAuthorId))
                    .thenThrow(BookNotFoundException.class);

            mockMvc.perform(patch("/api/books/{id}/author/{newAuthorId}", nonExistingBookId, newAuthorId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedBook)))
                    .andExpect(status().isNotFound());

        }

        @Test
        @DisplayName("Should return 404 when author does not exist")
        void shouldReturn404WhenAuthorDoesNotExist() throws Exception {
            Long bookId= 1L;
            Long nonExistingAuthorId=999L;

            author.setId(nonExistingAuthorId);
            updatedBook.setId(bookId);

            when(bookService.updateAuthor(bookId,nonExistingAuthorId))
                    .thenThrow(AuthorNotFoundException.class);

            mockMvc.perform(patch("/api/books/{id}/author/{newAuthorId}", bookId, nonExistingAuthorId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedBook)))
                    .andExpect(status().isNotFound());

        }

        @Test
        @DisplayName("Should return 400 when IDs are zero or negative")
        void shouldReturn400WhenIdsAreZeroOrNegative() throws Exception {

            mockMvc.perform(patch("/api/books/{id}/author/{newAuthorId}", 0, 1))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(patch("/api/books/{id}/author/{newAuthorId}", 1, 0))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(patch("/api/books/{id}/author/{newAuthorId}", -1, 1))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("DELETE /api/books/{id} - Delete Book")
    class DeleteBook {

        @Test
        @DisplayName("Should delete book when valid ID is provided")
        void shouldDeleteBookWhenValidIdIsProvided() throws Exception {
            Long bookId = 1L;
            doNothing().when(bookService).deleteById(bookId);

            mockMvc.perform(delete("/api/books/{id}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .andExpect(content().string("Book deleted successfully"));

            verify(bookService).deleteById(bookId);
        }

        @Test
        @DisplayName("Should return 404 when trying to delete non-existent book")
        void shouldReturn404WhenTryingToDeleteNonExistentBook() throws Exception {
            Long nonExistentId = 999L;
            doThrow(new BookNotFoundException("Book","Id","999L"))
                    .when(bookService).deleteById(nonExistentId);

            mockMvc.perform(delete("/api/books/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when ID is zero or negative")
        void shouldReturn400WhenIdIsZeroOrNegative() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/books/{id}", 0))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(delete("/api/books/{id}", -1))
                    .andExpect(status().isBadRequest());
        }
    }
}
