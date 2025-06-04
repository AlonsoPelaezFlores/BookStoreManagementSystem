package com.bookstore.management.books.repository;

import com.bookstore.management.books.model.Author;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    public AuthorRepository authorRepository;

    @Autowired
    public TestEntityManager entityManager;

    @Nested
    @DisplayName("Find authors by name")
    class FindByNameTest{




        @Test
        @DisplayName("Should find authors by partial name ignoring case")
        void findByPartialNameShouldFindGarcia() {
            Author garcia1 = new Author("Gabriel García Márquez", "Colombiana", LocalDate.of(1927, 3, 6));
            Author garcia2 = new Author("Federico García Lorca", "Española", LocalDate.of(1898, 6, 5));
            Author other = new Author("Mario Vargas Llosa", "Peruana", LocalDate.of(1936, 3, 28));
            entityManager.persist(garcia1);
            entityManager.persist(garcia2);
            entityManager.persist(other);
            entityManager.flush();

            List<Author> authorsFound = authorRepository.findByNameContainingIgnoreCase("garcía");

            assertThat(authorsFound)
                    .hasSize(2)
                    .extracting(Author::getName)
                    .containsExactlyInAnyOrder(
                            "Gabriel García Márquez",
                            "Federico García Lorca"
                    );
        }

        @Test
        @DisplayName("Should return empty list when no author match")
        void findByPartialNameShouldReturnEmptyList() {
            Author author = new Author("Mario Vargas Llosa", "Peruana", LocalDate.of(1936, 3, 28));
            entityManager.persist(author);
            entityManager.flush();

            List<Author> authorsFound = authorRepository.findByNameContainingIgnoreCase("shakespeare");
            assertThat(authorsFound).isEmpty();
        }
        @Test
        @DisplayName("Should handle case insensitive search correctly")
        void findByPartialNameShouldBeCaseInsensitive() {
            Author author = new Author("Gabriel García Márquez", "Colombiana", LocalDate.of(1927, 3, 6));
            entityManager.persist(author);
            entityManager.flush();

            assertThat(authorRepository.findByNameContainingIgnoreCase("GARCÍA")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("GarCía")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("gArcíA")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("garcía")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("garCÍA")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Find authors by nationality")
    class FindByNationalityTest{
        @Test
        @DisplayName("Should find spanish authors")
        void findByNationalityShouldFindSpanishAuthors(){
            Author spanish1 = new Author("Miguel de Cervantes", "Spanish", LocalDate.of(1547, 9, 29));
            Author spanish2 = new Author("Federico García Lorca", "Spanish", LocalDate.of(1898, 6, 5));
            Author colombian = new Author("Gabriel García Márquez", "Colombian ", LocalDate.of(1927, 3, 6));
            entityManager.persist(spanish1);
            entityManager.persist(spanish2);
            entityManager.persist(colombian);
            entityManager.flush();

            List<Author> authorsFound = authorRepository.findByNationality("Spanish");

            assertThat(authorsFound)
                    .hasSize(2)
                    .extracting(Author::getName)
                    .containsExactlyInAnyOrder("Miguel de Cervantes","Federico García Lorca");
        }
        @Test
        @DisplayName("return empty list when no author is found if the nationality does not match")
        void findByNationalityShouldReturnEmptyList() {

            Author author = new Author("Mario Vargas Llosa", "Peruvian", LocalDate.of(1936, 3, 28));
            entityManager.persist(author);
            entityManager.flush();
            List<Author> authorFound = authorRepository.findByNationality("Japanese");
            assertThat(authorFound).isEmpty();
        }
    }
}
