package com.bookstore.management.books.repository;

import com.bookstore.management.books.model.Author;
import com.bookstore.management.books.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    public BookRepository bookRepository;

    @Autowired
    public TestEntityManager entityManager;

    @Nested
    @DisplayName("Find books by ISBN")
    class FindBookByISBNTest{

        @Test
        @DisplayName("Should return book when ISBN exists")
        void shouldReturnBookWhenIsbnExists() {
            Author author  = new Author(
                    "Gabriel García Márquez",
                    "Colombian",
                    LocalDate.of(1927,3,6));

            Book book = new Book(
                    "978-0-06-088328-7",
                    "One Hundred Years of Solitude",
                    LocalDate.of(1967, 5, 30),
                    "The story of seven generations of the Buendía family in the mythical town of Macondo.",
                    417,
                    author);

            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.flush();

            Optional<Book> bookFound = bookRepository.findBookByISBN(book.getISBN());

            assertThat(bookFound).isNotNull();
            assertThat(bookFound).get().extracting(Book::getAuthor).isEqualTo(author);
            assertThat(bookFound.get()).isEqualTo(book);

        }
        @Test
        @DisplayName("Should return empty when isbn does not exist")
        void shouldReturnEmptyWhenIsbnDoesNotExist() {

            Optional<Book> bookFound = bookRepository.findBookByISBN("978-0-374-18082-8");
            assertThat(bookFound).isNotNull();
            assertThat(bookFound).isEmpty();

        }

    }

    @Nested
    @DisplayName("Find book by title")
    class FindBookByTitleTest {
        @Test
        @DisplayName("Should return book when exact title matches")
        void shouldReturnBookWhenExactTitleMatches() {
            Author author = new Author(
                    "Mario Vargas Llosa",
                    "Peruvian",
                    LocalDate.of(1936, 3, 28));
            Book book = new Book(
                    "978-0-374-18082-9",
                    "The Time of the Hero",
                    LocalDate.of(1963, 1, 1),
                    "A novel set in a military academy in Lima, Peru.",
                    419,
                    author);

            Book book2 = new Book(
                    "978-0-374-52700-8",
                    "Conversation in the Cathedral",
                    LocalDate.of(1969, 1, 1),
                    "A complex novel about politics and society in Peru.",
                    601,
                    author);
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.persist(book2);
            entityManager.flush();

            List<Book> bookFound = bookRepository.
                    findBookByTitleContainingIgnoreCase("The Time of the Hero");

            assertThat(bookFound).isNotNull();
            assertThat(bookFound).
                    hasSize(1).
                    extracting(Book::getTitle).
                    containsExactly("The Time of the Hero");
        }

        @Test
        @DisplayName("Should return books when the partial title matches")
        void shouldReturnEmptyWhenPartialTitleMatches() {
            Author author1 = new Author(
                    "Gabriel García Márquez",
                    "Colombian",
                    LocalDate.of(1927, 3, 6));

            Book book1 = new Book(
                    "978-0-06-088328-7",
                    "One Hundred Years of Solitude",
                    LocalDate.of(1967, 5, 30),
                    "The story of seven generations of the Buendía family in the mythical town of Macondo.",
                    417,
                    author1);
            entityManager.persist(author1);
            entityManager.persist(book1);
            entityManager.flush();

            Author author2 = new Author(
                    "Isabel Allende",
                    "Chilean",
                    LocalDate.of(1942, 8, 2));

            Book book2 = new Book(
                    "978-0-06-088329-4",
                    "One Hundred Years",
                    LocalDate.of(2020, 1, 1),
                    "A different book with similar title for testing partial matches.",
                    200,
                    author2);
            entityManager.persist(author2);
            entityManager.persist(book2);
            entityManager.flush();

            List<Book> booksFound = bookRepository.findBookByTitleContainingIgnoreCase("One Hundred Years");

            assertThat(booksFound).isNotNull();
            assertThat(booksFound).
                    hasSize(2).
                    extracting(Book::getTitle).
                    containsExactlyInAnyOrder("One Hundred Years", "One Hundred Years of Solitude");
        }

        @Test
        @DisplayName("Should return empty list when no title matches")
        void shouldReturnEmptyListWhenNoTitleMatches() {


            Author author = new Author(
                    "Jorge Luis Borges",
                    "Argentine",
                    LocalDate.of(1899, 8, 24));

            Book book = new Book(
                    "978-0-8112-0012-7",
                    "Labyrinths",
                    LocalDate.of(1962, 1, 1),
                    "A collection of short stories and essays exploring themes of infinity and reality.",
                    256,
                    author);
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.flush();

            List<Book> bookFound = bookRepository.findBookByTitleContainingIgnoreCase("Different Title");

            assertThat(bookFound).isNotNull();
            assertThat(bookFound).isEmpty();
        }
        @Test
        @DisplayName("Should return all books when the title is empty")
        void shouldReturnAllBooksWhenTheTitleIsEmpty() {
            Author author  = new Author(
                    "Gabriel García Márquez",
                    "Colombian",
                    LocalDate.of(1927,3,6));

            Book book1 = new Book(
                    "978-0-06-088328-7",
                    "One Hundred Years of Solitude",
                    LocalDate.of(1967, 5, 30),
                    "The story of seven generations of the Buendía family in the mythical town of Macondo.",
                    417,
                    author);

            Book book2 = new Book(
                    "978-0-06-114962-5",
                    "Love in the Time of Cholera",
                    LocalDate.of(1985, 9, 5),
                    "A love story that spans over fifty years in the Caribbean coast of Colombia.",
                    368,
                    author);

            Book book3 = new Book(
                    "978-0-06-093264-8",
                    "Chronicle of a Death Foretold",
                    LocalDate.of(1981, 4, 1),
                    "A novella about honor, fate, and collective responsibility in a small town.",
                    120,
                    author);
            entityManager.persist(author);
            entityManager.persist(book1);
            entityManager.persist(book2);
            entityManager.persist(book3);
            entityManager.flush();

            List<Book> booksFound = bookRepository.findBookByTitleContainingIgnoreCase("");

            assertThat(booksFound).isNotNull();
            assertThat(booksFound).isNotEmpty();
            assertThat(booksFound).
                    hasSize(3).
                    extracting(Book::getISBN).
                    containsExactlyInAnyOrder("978-0-06-088328-7","978-0-06-114962-5","978-0-06-093264-8");


        }

        @Test
        @DisplayName("Should return book when search is case insensitive")
        void shouldReturnBookWhenSearchIsCaseInsensitive() {

            Author author  = new Author(
                    "Gabriel García Márquez",
                    "Colombian",
                    LocalDate.of(1927,3,6));
            Book book = new Book(
                    "978-0-06-088328-7",
                    "One Hundred Years of Solitude",
                    LocalDate.of(1967, 5, 30),
                    "The story of seven generations of the Buendía family in the mythical town of Macondo.",
                    417,
                    author);
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.flush();

            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("ONE HUNDRED YEARS OF SOLITUDE")).hasSize(1);
            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("one hundred years of solitude")).hasSize(1);
            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("oNE hUNDred YEArs oF SOLITUDE")).hasSize(1);
            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("one HUNDRED years OF solitude")).hasSize(1);
            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("OnE hUnDrEd yEaRs Of SoLiTuDe")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Find books by author id")
    class FindBooksByAuthorIdTest{

        @Test
        @DisplayName("Should return books when author ID exists")
        void shouldReturnBooksWhenAuthorIdExists() {
            Author author = new Author(
                    "Jorge Luis Borges",
                    "Argentine",
                    LocalDate.of(1899,8,24));

            Book book = new Book(
                    "978-0-8112-0012-7",
                    "Labyrinths",
                    LocalDate.of(1962, 1, 1),
                    "A collection of short stories and essays exploring themes of infinity and reality.",
                    256,
                    author);

            Book book2 = new Book(
                    "978-0-8112-1012-6",
                    "Ficciones",
                    LocalDate.of(1944, 1, 1),
                    "A collection of fantastical short stories that challenge conventional narrative.",
                    174,
                    author);
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.persist(book2);
            entityManager.flush();

            List<Book> booksFound = bookRepository.findBooksByAuthorId(author.getId());


            assertThat(booksFound).isNotEmpty();
            assertThat(booksFound).
                    hasSize(2).
                    extracting(Book::getTitle).
                    containsExactlyInAnyOrder("Ficciones", "Labyrinths");

        }

        @Test
        @DisplayName("Should return empty list when author ID does not exists")
        void shouldReturnEmptyListWhenAuthorIdDoesNotExist() {

            List<Book> booksFound = bookRepository.findBooksByAuthorId(2L);

            assertThat(booksFound).isNotNull();
            assertThat(booksFound).isEmpty();
        }
        @Test
        @DisplayName("Should return empty list when author no has books")
        void shouldReturnEmptyListWhenAuthorHasNoBooks() {
            Author author  = new Author(
                    "Mario Vargas Llosa",
                    "Peruvian",
                    LocalDate.of(1936,3,28));
            entityManager.persist(author);
            entityManager.flush();

            List<Book> booksFound = bookRepository.findBooksByAuthorId(author.getId());

            assertThat(booksFound).isNotNull();
            assertThat(booksFound).hasSize(0);
        }
        @Test
        @DisplayName("Should return empty list when author ID is negative")
        void shouldReturnEmptyListWhenAuthorIdIsNegative() {

            List<Book> booksFound = bookRepository.findBooksByAuthorId(-2L);

            assertThat(booksFound).isNotNull();
            assertThat(booksFound).isEmpty();
        }

    }
}
