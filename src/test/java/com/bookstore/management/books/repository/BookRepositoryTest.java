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
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("Find books by ISBN")
    class FindBookByISBNTest{

        @Test
        @DisplayName("Should return book when ISBN exists")
        void shouldReturnBookWhenIsbnExists() {

            Author author = Author.builder()
                    .name("John Doe")
                    .nationality("Americano")
                    .birthDate(LocalDate.of(1980,5,15))
                    .gender(Author.Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();

            Book book = Book.builder()
                    .isbn("978-0-06-088328-7")
                    .title("Orgullo y prejuicio")
                    .publishDate(LocalDate.of(1813, 1, 28))
                    .description("Una novela romántica que explora los temas del amor, la reputación y la clase social en la Inglaterra del siglo XIX.")
                    .pages(432)
                    .genre("Romance")
                    .author(author)
                    .build();

            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.flush();

            Optional<Book> bookFound = bookRepository.findBookByIsbn(book.getIsbn());

            assertThat(bookFound).isNotNull();
            assertThat(bookFound).get().extracting(Book::getAuthor).isEqualTo(author);
            assertThat(bookFound.get()).isEqualTo(book);

        }
        @Test
        @DisplayName("Should return empty when isbn does not exist")
        void shouldReturnEmptyWhenIsbnDoesNotExist() {

            Optional<Book> bookFound = bookRepository.findBookByIsbn("978-0-374-18082-8");
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
            Author author = Author.builder()
                    .name("Mario Vargas Llosa")
                    .nationality("Peruano")
                    .birthDate(LocalDate.of(1936, 3, 28))
                    .gender(Author.Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();

            Book book = Book.builder()
                    .isbn("978-0-374-18082-9")
                    .title("El tiempo del heroe")
                    .publishDate(LocalDate.of(1813, 1, 28))
                    .description("Una novela ambientada en una academia militar en Lima, Peru")
                    .pages(419)
                    .genre("Militar")
                    .author(author)
                    .build();
            Book book2 = Book.builder()
                    .isbn("978-84-376-2181-4")
                    .title("Conversacion en la Catedral")
                    .publishDate(LocalDate.of(1813, 1, 28))
                    .description("Una compleja novela acerca de politicos y una sociedad en Peru")
                    .pages(601)
                    .genre("Politica")
                    .author(author)
                    .build();

            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.persist(book2);
            entityManager.flush();

            List<Book> bookFound = bookRepository.
                    findBookByTitleContainingIgnoreCase("El tiempo del heroe");

            assertThat(bookFound).isNotNull();
            assertThat(bookFound).
                    hasSize(1).
                    extracting(Book::getTitle).
                    containsExactly("El tiempo del heroe");
        }

        @Test
        @DisplayName("Should return books when the partial title matches")
        void shouldReturnEmptyWhenPartialTitleMatches() {
            Author author = Author.builder()
                    .name("Mario Vargas Llosa")
                    .nationality("Peruano")
                    .birthDate(LocalDate.of(1936, 3, 28))
                    .gender(Author.Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();

            Book book = Book.builder()
                    .isbn("978-0-374-18082-9")
                    .title("El tiempo del heroe")
                    .publishDate(LocalDate.of(1813, 1, 28))
                    .description("Una novela ambientada en una academia militar en Lima, Peru")
                    .pages(419)
                    .genre("Militar")
                    .author(author)
                    .build();
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.flush();

            Author author2 = Author.builder()
                    .name("Isabel Allende")
                    .nationality("Chilena")
                    .birthDate(LocalDate.of(1942, 8, 2))
                    .gender(Author.Gender.FEMALE)
                    .biography("Famosa Novelista")
                    .build();

            Book book2 = Book.builder()
                    .isbn("978-84-376-2181-4")
                    .title("El tiempo escondido")
                    .publishDate(LocalDate.of(1965, 8, 1))
                    .description("Una épica de ciencia ficción ambientada en el planeta desértico Arrakis, hogar de la especia más valiosa del universo.")
                    .pages(688)
                    .genre("Ciencia ficción")
                    .author(author2)
                    .build();
            entityManager.persist(author2);
            entityManager.persist(book2);
            entityManager.flush();

            List<Book> booksFound = bookRepository.findBookByTitleContainingIgnoreCase("El tiempo");

            assertThat(booksFound).isNotNull();
            assertThat(booksFound).
                    hasSize(2).
                    extracting(Book::getTitle).
                    containsExactlyInAnyOrder("El tiempo del heroe", "El tiempo escondido");
        }

        @Test
        @DisplayName("Should return empty list when no title matches")
        void shouldReturnEmptyListWhenNoTitleMatches() {

            Author author = Author.builder()
                    .name("Isabel Allende")
                    .nationality("Chilena")
                    .birthDate(LocalDate.of(1942, 8, 2))
                    .gender(Author.Gender.FEMALE)
                    .biography("Famosa Novelista")
                    .build();

            Book book = Book.builder()
                    .isbn("978-84-376-2181-4")
                    .title("El tiempo escondido")
                    .publishDate(LocalDate.of(1965, 8, 1))
                    .description("Una épica de ciencia ficción ambientada en el planeta desértico Arrakis, hogar de la especia más valiosa del universo.")
                    .pages(688)
                    .genre("Ciencia ficción")
                    .author(author)
                    .build();
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.flush();

            List<Book> bookFound = bookRepository.findBookByTitleContainingIgnoreCase("Titulo diferente");

            assertThat(bookFound).isNotNull();
            assertThat(bookFound).isEmpty();
        }
        @Test
        @DisplayName("Should return all books when the title is empty")
        void shouldReturnAllBooksWhenTheTitleIsEmpty() {
            Author author = Author.builder()
                    .name("Gabriel Garcia Marquezx")
                    .nationality("Colombiano")
                    .birthDate(LocalDate.of(1927,3,6))
                    .gender(Author.Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();

            Book book = Book.builder()
                    .isbn("0439708184")
                    .title("Harry Potter y la piedra filosofal")
                    .publishDate(LocalDate.of(1997, 6, 26))
                    .description("La historia de un joven mago que descubre su herencia mágica y su destino en el mundo de la hechicería.")
                    .pages(223)
                    .genre("Fantasía juvenil")
                    .author(author)
                    .build();
            Book book2 = Book.builder()
                    .isbn("9780307474278")
                    .title("El código Da Vinci")
                    .publishDate(LocalDate.of(2003, 3, 18))
                    .description("Un thriller que combina arte, historia y conspiración mientras el protagonista resuelve un misterio ancestral.")
                    .pages(489)
                    .genre("Thriller")
                    .author(author)
                    .build();

            Book book3 = Book.builder()
                    .isbn("0143034901")
                    .title("La sombra del viento")
                    .publishDate(LocalDate.of(2001, 4, 17))
                    .description("Una novela ambientada en la Barcelona de posguerra que gira en torno a un misterioso libro y su autor desaparecido.")
                    .pages(565)
                    .genre("Misterio")
                    .author(author)
                    .build();
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.persist(book2);
            entityManager.persist(book3);
            entityManager.flush();

            List<Book> booksFound = bookRepository.findBookByTitleContainingIgnoreCase("");

            assertThat(booksFound).isNotNull();
            assertThat(booksFound).isNotEmpty();
            assertThat(booksFound).
                    hasSize(3).
                    extracting(Book::getIsbn).
                    containsExactlyInAnyOrder("0439708184","9780307474278","0143034901");


        }

        @Test
        @DisplayName("Should return book when search is case insensitive")
        void shouldReturnBookWhenSearchIsCaseInsensitive() {

            Author author = Author.builder()
                    .name("Gabriel Garcia Marquezx")
                    .nationality("Colombiano")
                    .birthDate(LocalDate.of(1927,3,6))
                    .gender(Author.Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();

            Book book = Book.builder()
                    .isbn("0439708184")
                    .title("Harry Potter y la piedra filosofal")
                    .publishDate(LocalDate.of(1997, 6, 26))
                    .description("La historia de un joven mago que descubre su herencia mágica y su destino en el mundo de la hechicería.")
                    .pages(223)
                    .genre("Fantasía juvenil")
                    .author(author)
                    .build();
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.flush();

            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("HARRY POTTER Y LA PIEDRA FILOSOFAL")).hasSize(1);
            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("harry potter y la piedra filosofal")).hasSize(1);
            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("HARRY potter Y la PIEDRA filosofal")).hasSize(1);
            assertThat(bookRepository.findBookByTitleContainingIgnoreCase("HaRrY pOtTeR y lA pIeDrA fIlOsOfAL")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Find books by author id")
    class FindBooksByAuthorIdTest{

        @Test
        @DisplayName("Should return books when author ID exists")
        void shouldReturnBooksWhenAuthorIdExists() {
            Author author = Author.builder()
                    .name("Gabriel Garcia Marquezx")
                    .nationality("Colombiano")
                    .birthDate(LocalDate.of(1927,3,6))
                    .gender(Author.Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();

            Book book = Book.builder()
                    .isbn("0439708184")
                    .title("Harry Potter y la piedra filosofal")
                    .publishDate(LocalDate.of(1997, 6, 26))
                    .description("La historia de un joven mago que descubre su herencia mágica y su destino en el mundo de la hechicería.")
                    .pages(223)
                    .genre("Fantasía juvenil")
                    .author(author)
                    .build();
            Book book2 = Book.builder()
                    .isbn("9780307474278")
                    .title("El código Da Vinci")
                    .publishDate(LocalDate.of(2003, 3, 18))
                    .description("Un thriller que combina arte, historia y conspiración mientras el protagonista resuelve un misterio ancestral.")
                    .pages(489)
                    .genre("Thriller")
                    .author(author)
                    .build();
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.persist(book2);
            entityManager.flush();

            List<Book> booksFound = bookRepository.findBooksByAuthorId(author.getId());


            assertThat(booksFound).isNotEmpty();
            assertThat(booksFound).
                    hasSize(2).
                    extracting(Book::getTitle).
                    containsExactlyInAnyOrder("Harry Potter y la piedra filosofal", "El código Da Vinci");

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
            Author author = Author.builder()
                    .name("Gabriel Garcia Marquezx")
                    .nationality("Colombiano")
                    .birthDate(LocalDate.of(1927,3,6))
                    .gender(Author.Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();
            entityManager.persistAndFlush(author);

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
