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
    @DisplayName("Find authors by name test")
    class FindByNameTest{

        @Test
        @DisplayName("Should return authors by partial name")
        void shouldReturnAuthor_WhenFindPartialName() {
            Author author = Author.builder()
                    .name("Gabriel García Márquez")
                    .nationality("Colombian")
                    .dateOfBirth(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Escritor colombiano, premio Nobel de Literatura en 1982. Máximo exponente del realismo mágico y autor de obras como 'Cien años de soledad'. Su obra ha sido traducida a múltiples idiomas.")
                    .build();

            Author author2 = Author.builder()
                    .name("Cristina García López")
                    .nationality("Cuban-American")
                    .dateOfBirth(LocalDate.of(1958, 7, 4))
                    .gender(Author.Gender.FEMALE)
                    .biography("Novelista cubano-americana conocida por su exploración de temas de identidad cultural y experiencia inmigrante. Sus obras han sido aclamadas por la crítica internacional.")
                    .build();

            Author author3 = Author.builder()
                    .name("George Orwell")
                    .nationality("British")
                    .dateOfBirth(LocalDate.of(1903, 6, 25))
                    .gender(Author.Gender.MALE)
                    .biography("Escritor británico famoso por sus novelas distópicas '1984' y 'Rebelión en la granja'. Crítico social y político, sus obras exploran temas de totalitarismo y control social.")
                    .build();
            entityManager.persist(author);
            entityManager.persist(author2);
            entityManager.persist(author3);
            entityManager.flush();

            List<Author> authorsFound = authorRepository.findByNameContainingIgnoreCase("García");

            assertThat(authorsFound)
                    .hasSize(2)
                    .extracting(Author::getName)
                    .containsExactlyInAnyOrder(
                            "Gabriel García Márquez",
                            "Cristina García López"
                    );
        }

        @Test
        @DisplayName("Should return empty list when no author match")
        void shouldReturnEmpty_WhenNoAuthorMatch() {
            Author author = Author.builder()
                    .name("George Orwell")
                    .nationality("British")
                    .dateOfBirth(LocalDate.of(1903, 6, 25))
                    .gender(Author.Gender.MALE)
                    .biography("Escritor británico famoso por sus novelas distópicas '1984' y 'Rebelión en la granja'. Crítico social y político, sus obras exploran temas de totalitarismo y control social.")
                    .build();
            entityManager.persistAndFlush(author);

            List<Author> authorsFound = authorRepository.findByNameContainingIgnoreCase("shakespeare");

            assertThat(authorsFound).isNotNull();
            assertThat(authorsFound).isEmpty();
        }
        @Test
        @DisplayName("Should handle case insensitive search correctly")
        void shouldReturnAuthor_whenNotCaseInsensitive() {
            Author author = Author.builder()
                    .name("Cristina García López")
                    .nationality("Cuban-American")
                    .dateOfBirth(LocalDate.of(1958, 7, 4))
                    .gender(Author.Gender.FEMALE)
                    .biography("Novelista cubano-americana conocida por su exploración de temas de identidad cultural y experiencia inmigrante. Sus obras han sido aclamadas por la crítica internacional.")
                    .build();

            entityManager.persistAndFlush(author);

            assertThat(authorRepository.findByNameContainingIgnoreCase("GARCÍA")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("GarCía")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("gArcíA")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("garcía")).hasSize(1);
            assertThat(authorRepository.findByNameContainingIgnoreCase("garCÍA")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Find authors by nationality test")
    class FindByNationalityTest{
        @Test
        @DisplayName("Should return author by nationality")
        void shouldReturnAuthorWhenNationalityMatches() {
            Author author = Author.builder()
                    .name("Jane Austen")
                    .nationality("British")
                    .dateOfBirth(LocalDate.of(1775, 12, 16))
                    .gender(Author.Gender.FEMALE)
                    .biography("Novelista británica conocida por sus agudas observaciones sociales y su wit. Sus obras como 'Orgullo y prejuicio' siguen siendo populares más de dos siglos después.")
                    .build();

            Author author2 = Author.builder()
                    .name("George Orwell")
                    .nationality("British")
                    .dateOfBirth(LocalDate.of(1903, 6, 25))
                    .gender(Author.Gender.MALE)
                    .biography("Escritor británico famoso por sus novelas distópicas '1984' y 'Rebelión en la granja'. Crítico social y político, sus obras exploran temas de totalitarismo y control social.")
                    .build();
            entityManager.persist(author);
            entityManager.persist(author2);
            entityManager.flush();

            List<Author> authorsFound = authorRepository.findByNationality("British");

            assertThat(authorsFound)
                    .hasSize(2)
                    .extracting(Author::getName)
                    .containsExactlyInAnyOrder("George Orwell","Jane Austen");
        }
        @Test
        @DisplayName("Should return empty when nationality does not matches")
        void shouldReturnEmptyWhenNationalityDoesNotMatches() {
            Author author = Author.builder()
                    .name("Jane Austen")
                    .nationality("British")
                    .dateOfBirth(LocalDate.of(1775, 12, 16))
                    .gender(Author.Gender.FEMALE)
                    .biography("Novelista británica conocida por sus agudas observaciones sociales y su wit. Sus obras como 'Orgullo y prejuicio' siguen siendo populares más de dos siglos después.")
                    .build();
            entityManager.persistAndFlush(author);
            List<Author> authorFound = authorRepository.findByNationality("Japanese");
            assertThat(authorFound).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find authors by gender test")
    class FindByGenderTest{
        @BeforeEach
        void beforeEach() {

            Author author = Author.builder()
                    .name("Frank Herbert")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1920, 10, 8))
                    .gender(Author.Gender.MALE)
                    .biography("Escritor estadounidense de ciencia ficción, creador de la épica saga 'Dune'. Periodista y ecologista, incorporó temas ambientales y políticos en sus obras futuristas.")
                    .build();
            Author author2 = Author.builder()
                    .name("Carlos Martínez Fernández")
                    .nationality("Spanish")
                    .dateOfBirth(LocalDate.of(1971, 9, 22))
                    .gender(Author.Gender.MALE)
                    .biography("Novelista español conocido por sus thrillers psicológicos y novelas de misterio. Ha ganado varios premios literarios nacionales por su contribución al género negro español.")
                    .build();

            Author author3 = Author.builder()
                    .name("Harper Lee")
                    .nationality("American")
                    .dateOfBirth(LocalDate.of(1926, 4, 28))
                    .gender(Author.Gender.FEMALE)
                    .biography("Escritora estadounidense famosa por su novela 'Matar a un ruiseñor'. Recibió el Premio Pulitzer por su única obra publicada durante décadas, convirtiéndose en un clásico de la literatura.")
                    .build();

            Author author4 = Author.builder()
                    .name("J.K. Rowling")
                    .nationality("British")
                    .dateOfBirth(LocalDate.of(1965, 7, 31))
                    .gender(Author.Gender.PREFER_NOT_TO_SAY)
                    .biography("Escritora británica creadora de la saga Harry Potter. Una de las autoras más exitosas de la historia, sus libros han sido traducidos a más de 80 idiomas y adaptados al cine.")
                    .build();
            entityManager.persist(author);
            entityManager.persist(author2);
            entityManager.persist(author3);
            entityManager.persist(author4);
            entityManager.flush();
        }

        @Test
        @DisplayName("Should return male authors when male gender is provided")
        void shouldReturnMaleAuthorsWhenMaleGenderProvided() {

            List<Author> maleAuthors = authorRepository.findByGender(Author.Gender.MALE);

            assertThat(maleAuthors)
                    .hasSize(2)
                    .extracting(Author::getName)
                    .containsExactlyInAnyOrder("Frank Herbert","Carlos Martínez Fernández");

        }

        @Test
        @DisplayName("Should return female authors when female gender is provided")
        void shouldReturnFemaleAuthorsWhenFemaleGenderProvided() {

            List<Author> femaleAuthors = authorRepository.findByGender(Author.Gender.FEMALE);

            assertThat(femaleAuthors)
                    .hasSize(1)
                    .extracting(Author::getName)
                    .containsExactlyInAnyOrder("Harper Lee");
        }

        @Test
        @DisplayName("Should return prefer not to say gender when prefer not to say gender is provided")
        void shouldReturnPreferNotToSayAuthorsWhenPreferNotToSayGenderProvided() {

            List<Author> preferNotToSayAuthors = authorRepository.findByGender(Author.Gender.PREFER_NOT_TO_SAY);

            assertThat(preferNotToSayAuthors)
                    .hasSize(1)
                    .extracting(Author::getName)
                    .containsExactlyInAnyOrder("J.K. Rowling");
        }
    }
}
