package com.bookstore.management.book.service;

import com.bookstore.management.book.dto.CreateAuthorDTO;
import com.bookstore.management.book.mapper.AuthorMapper;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.repository.AuthorRepository;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @Mock
    private AuthorRepository authorRepository;
    @Spy
    private AuthorMapper authorMapper = Mappers.getMapper(AuthorMapper.class);
    @InjectMocks
    private AuthorService authorService;

    @Nested
    class findAll{
        @Test
        @DisplayName("Should return all authors when authors exist")
        void shouldReturnAllAuthorsWhenAuthorsExist() {
            Author author= Author.builder()
                    .id(1L)
                    .name("Gabriel García Márquez")
                    .nationality("Colombian")
                    .birthDate(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist, short-story writer, screenwriter, and journalist, known affectionately as Gabo or Gabito throughout Latin America.")
                    .build();
            Author author2 = Author.builder()
                    .id(2L)
                    .name("Jane Austen")
                    .nationality("British")
                    .birthDate(LocalDate.of(1775, 12, 16))
                    .gender(Author.Gender.FEMALE)
                    .biography("English novelist known primarily for her six major novels, which interpret, critique and comment upon the British landed gentry at the end of the 18th century.")
                    .build();

            List<Author> expectAuthors= Arrays.asList(author, author2);
            when(authorRepository.findAll()).thenReturn(expectAuthors);

            List<Author> actualAuthors = authorService.findAll();

            assertThat(actualAuthors).hasSize(2);
            assertThat(actualAuthors).isEqualTo(expectAuthors);
        }
        @Test
        @DisplayName("Should return empty list when no authors exist")
        void shouldReturnEmptyListWhenNoAuthorsExist(){
            List<Author> expectAuthors = Collections.emptyList();
            when(authorRepository.findAll()).thenReturn(expectAuthors);

            List<Author> actualAuthors = authorService.findAll();

            assertThat(actualAuthors).hasSize(0);
        }
    }
    @Nested
    class findById{
        @Test
        @DisplayName("Should return author when author exist")
        void shouldReturnAuthorWhenAuthorExists(){
            Author author= Author.builder()
                    .id(1L)
                    .name("Gabriel García Márquez")
                    .nationality("Colombian")
                    .birthDate(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist, short-story writer, screenwriter, and journalist, known affectionately as Gabo or Gabito throughout Latin America.")
                    .build();
            when(authorRepository.findById(1L)).thenReturn(Optional.ofNullable(author));

            Author actualAuthor = authorService.findById(1L);
            assertThat(actualAuthor).isEqualTo(author);
        }
        @Test
        @DisplayName("Should throw author not found exception when author not found")
        void shouldThrowAuthorNotFoundExceptionWhenAuthorNotFound(){

            Long nonExistentId = 888L;
            when(authorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(()-> authorService.findById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Author")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("888");
            verify(authorRepository).findById(nonExistentId);
        }
    }
    @Nested
    class createAuthor{
        private CreateAuthorDTO createAuthorDto;
        private Author savedAuthor;

        @BeforeEach
        void setUp(){

            createAuthorDto = CreateAuthorDTO.builder()
                    .name("Gabriel García Márquez")
                    .nationality("Colombian")
                    .birthDate(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist and Nobel Prize winner.")
                    .build();
            savedAuthor = Author.builder()
                    .id(1L)
                    .name("Gabriel García Márquez")
                    .nationality("Colombian")
                    .birthDate(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist and Nobel Prize winner.")
                    .build();
        }
        @Test
        @DisplayName("Should return author saved when creating author with valid data")
        void shouldReturnSavedAuthorWithValidData(){

            when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

            Author actualAuthor = authorService.createAuthor(createAuthorDto);

            assertThat(actualAuthor).isNotNull();
            assertThat(actualAuthor.getId()).isEqualTo(1L);
            assertThat(actualAuthor.getName()).isEqualTo("Gabriel García Márquez");
            assertThat(actualAuthor.getNationality()).isEqualTo("Colombian");
            assertThat(actualAuthor.getBirthDate()).isEqualTo(LocalDate.of(1927,3,6));
            assertThat(actualAuthor.getGender()).isEqualTo(Author.Gender.MALE);
            assertThat(actualAuthor.getBiography()).isEqualTo("Colombian novelist and Nobel Prize winner.");

        }
        @Test
        @DisplayName("Should call save repository when creating a new author with valid data")
        void shouldCallRepositorySaveWithValidData(){

            when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

            authorService.createAuthor(createAuthorDto);

            ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
            verify(authorRepository).save(authorCaptor.capture());

            Author capturedAuthor = authorCaptor.getValue();
            assertThat(capturedAuthor.getName()).isEqualTo("Gabriel García Márquez");
            assertThat(capturedAuthor.getNationality()).isEqualTo("Colombian");
            assertThat(capturedAuthor.getBirthDate()).isEqualTo(LocalDate.of(1927, 3, 6));
            assertThat(capturedAuthor.getGender()).isEqualTo(Author.Gender.MALE);
            assertThat(capturedAuthor.getBiography()).isEqualTo("Colombian novelist and Nobel Prize winner.");
            assertThat(capturedAuthor.getId()).isNull();

        }
    }
    @Nested
    class updateAuthor{

        private CreateAuthorDTO createAuthorDto;
        private Author author;
        private Author expectAuthor;

        @BeforeEach
        void setUp(){
            createAuthorDto = CreateAuthorDTO.builder()
                    .name("Gabriel")
                    .nationality("Colombian")
                    .birthDate(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist.")
                    .build();
            author = Author.builder()
                    .id(1L)
                    .name("Gabriel García Márquez")
                    .nationality("Colombian")
                    .birthDate(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist and Nobel Prize winner.")
                    .build();
            expectAuthor = Author.builder()
                    .id(1L)
                    .name("Gabriel")
                    .nationality("Colombian")
                    .birthDate(LocalDate.of(1927, 3, 6))
                    .gender(Author.Gender.MALE)
                    .biography("Colombian novelist.")
                    .build();


        }
        @Test
        @DisplayName("Should return update author when author exist")
        void shouldReturnUpdatedAuthorWhenAuthorExists(){

            Long authorId = 1L;

            when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
            when(authorRepository.save(any(Author.class))).thenReturn(expectAuthor);

            Author actualAuthor = authorService.updateAuthor(createAuthorDto, authorId);

            assertThat(actualAuthor).isNotNull();

            verify(authorRepository).save(any(Author.class));
            verify(authorRepository).findById(authorId);
            verify(authorMapper).updateEntityFromDto(createAuthorDto, author);

        }
        @Test
        @DisplayName("Should throw author not found exception when author not found")
        void shouldThrowAuthorNotFoundExceptionWhenAuthorNotFound(){
            Long nonExistentId = 999L;

            when(authorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(()-> authorService.updateAuthor(createAuthorDto, nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Author")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

        }
        @Test
        @DisplayName("Should update all fields of the author with valid data")
        void shouldUpdateAllFieldsWithValidData(){
            Long authorId = 1L;

            when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
            when(authorRepository.save(any(Author.class))).thenReturn(expectAuthor);

            Author actualAuthor = authorService.updateAuthor(createAuthorDto, authorId);

            assertThat(actualAuthor.getName()).isEqualTo(expectAuthor.getName());
            assertThat(actualAuthor.getNationality()).isEqualTo(expectAuthor.getNationality());
            assertThat(actualAuthor.getBirthDate()).isEqualTo(expectAuthor.getBirthDate());
            assertThat(actualAuthor.getGender()).isEqualTo(expectAuthor.getGender());
            assertThat(actualAuthor.getBiography()).isEqualTo(expectAuthor.getBiography());
        }
        @Test
        @DisplayName("Should call save repository")
        void shouldCallRepositorySaveWithValidData(){
            Long authorId = 1L;

            when(authorRepository.findById(authorId)).thenReturn(Optional.ofNullable(author));
            when(authorRepository.save(any(Author.class))).thenReturn(expectAuthor);

            authorService.updateAuthor(createAuthorDto, authorId);

            ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
            verify(authorRepository).save(authorCaptor.capture());

            Author capturedAuthor = authorCaptor.getValue();
            assertThat(capturedAuthor.getName()).isEqualTo("Gabriel");
            assertThat(capturedAuthor.getNationality()).isEqualTo("Colombian");
            assertThat(capturedAuthor.getBirthDate()).isEqualTo(LocalDate.of(1927, 3, 6));
            assertThat(capturedAuthor.getGender()).isEqualTo(Author.Gender.MALE);
            assertThat(capturedAuthor.getBiography()).isEqualTo("Colombian novelist.");
        }
    }
    @Nested
    class deleteAuthorById{

        @Test
        @DisplayName("Should delete successfully when author exists")
        void shouldDeleteSuccessfullyWhenAuthorExists(){
            Long authorId = 1L;

            when(authorRepository.existsById(authorId)).thenReturn(true);

            authorService.deleteAuthorById(authorId);

            verify(authorRepository).existsById(authorId);
            verify(authorRepository).deleteById(authorId);

        }
        @Test
        @DisplayName("Should throw author not found exception when author not found")
        void shouldThrowAuthorNotFoundExceptionWhenAuthorNotFound(){
            Long nonExistentId = 999L;

            when(authorRepository.existsById(nonExistentId)).thenReturn(false);

            assertThatThrownBy(()-> authorService.deleteAuthorById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Author")
                    .hasMessageContaining("Id")
                    .hasMessageContaining("999");

            verify(authorRepository).existsById(nonExistentId);
            verify(authorRepository, never()).deleteById(any(Long.class));
        }
        @Test
        @DisplayName("Should call repository delete by id ")
        void shouldCallRepositoryDeleteById(){
            Long authorId = 1L;
            when(authorRepository.existsById(anyLong())).thenReturn(true);

            authorService.deleteAuthorById(authorId);

            verify(authorRepository).deleteById(authorId);
        }
    }


}