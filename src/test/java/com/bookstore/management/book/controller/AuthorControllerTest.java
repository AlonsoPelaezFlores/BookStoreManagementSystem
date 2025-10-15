package com.bookstore.management.book.controller;

import com.bookstore.management.book.dto.CreateAuthorDTO;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.service.AuthorService;
import com.bookstore.management.shared.exception.custom.AuthorNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(controllers = AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DisplayName("Author Controller Test")
class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthorService authorService;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/authors - Find all authors")
    class FindAllAuthors{
        @Test
        @DisplayName("Should return all author when authors exist")
        void shouldReturnAllAuthorsWhenAuthorsExist() throws Exception {


            Author author1 = Author.builder()
                    .id(1L)
                    .name("John Doe")
                    .nationality("American")
                    .birthDate(LocalDate.of(1980,5,15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();
            Author author2 = Author.builder()
                    .id(2L)
                    .name("Jane Smith")
                    .nationality("British")
                    .birthDate(LocalDate.of(1975, 3, 22))
                    .gender(Author.Gender.FEMALE)
                    .biography("Bestselling author")
                    .build();

            List<Author> authors = Arrays.asList(author1, author2);

            when(authorService.findAll()).thenReturn(authors);

            mockMvc.perform(get("/api/authors"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("John Doe"))
                    .andExpect(jsonPath("$[0].nationality").value("American"))
                    .andExpect(jsonPath("$[0].gender").value("MALE"))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                    .andExpect(jsonPath("$[1].nationality").value("British"))
                    .andExpect(jsonPath("$[1].gender").value("FEMALE"));
        }
        @Test
        @DisplayName("Should return empty list when no authors exist")
        void shouldReturnEmptyListWhenNoAuthorsExist() throws Exception{
            when(authorService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/authors"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
    @Nested
    @DisplayName("GET /api/authors/{id} - Find Author By ID")
    class FindAuthorById{
        @Test
        @DisplayName("Should return author when valid ID is provided")
        void shouldReturnAuthorWhenValidIdIsProvided() throws Exception{
            Long authorId = 1L;
            Author author = Author.builder()
                    .id(authorId)
                    .name("John Doe")
                    .nationality("American")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            when(authorService.findById(authorId)).thenReturn(author);

            mockMvc.perform(get("/api/authors/{id}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(authorId))
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.nationality").value("American"))
                    .andExpect(jsonPath("$.gender").value("MALE"))
                    .andExpect(jsonPath("$.biography").value("famous novelist"));

        }

        @Test
        @DisplayName("Should return 404 when author with ID does not exist")
        void shouldReturn404WhenAuthorWithIdDoesNotExist() throws Exception{
            Long nonExistingAuthorId = 999L;

            when(authorService.findById(nonExistingAuthorId)).thenThrow(AuthorNotFoundException.class);

            mockMvc.perform(get("/api/authors/{id}", nonExistingAuthorId))
                    .andExpect(status().isNotFound());

        }
        @Test
        @DisplayName("Should return 400 when invalid ID format is provided")
        void shouldReturn400WhenInvalidIdFormatIsProvided() throws Exception {

            String invalidId = "invalid";

            mockMvc.perform(get("/api/authors/{id}", invalidId))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("POST /api/authors - Create Author")
    class CreateAuthor {
        @Test
        @DisplayName("Should create author when valid data is provided")
        void shouldCreateAuthorWhenValidDataIsProvided() throws Exception{
            CreateAuthorDTO createAuthorDto = CreateAuthorDTO.builder()
                    .name("John Doe")
                    .nationality("American")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();
            Author createdAuthor = Author.builder()
                    .id(1L)
                    .name("John Doe")
                    .nationality("American")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            when(authorService.createAuthor(any(CreateAuthorDTO.class))).thenReturn(createdAuthor);

            mockMvc.perform(post("/api/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAuthorDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L));

            verify(authorService).createAuthor(any(CreateAuthorDTO.class));

        }

        @Test
        @DisplayName("Should return 400 when invalid data is provided")
        void shouldReturn400WhenInvalidDataIsProvided() throws Exception {

            CreateAuthorDTO invalidCreateAuthorDTO = new CreateAuthorDTO();

            mockMvc.perform(post("/api/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidCreateAuthorDTO)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() throws Exception {

            CreateAuthorDTO invalidCreateAuthorDTO = CreateAuthorDTO.builder()
                    .nationality("American")
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();

            mockMvc.perform(post("/api/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCreateAuthorDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when nationality is missing")
        void shouldReturn400WhenNationalityIsMissing() throws Exception {

            CreateAuthorDTO invalidCreateAuthorDTO = CreateAuthorDTO.builder()
                    .name("John Doe")
                    .gender(Author.Gender.MALE)
                    .biography("famous novelist")
                    .build();
            mockMvc.perform(post("/api/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCreateAuthorDTO)))
                    .andExpect(status().isBadRequest());
        }

    }
    @Nested
    @DisplayName("PUT /api/authors/{id} - Update Author")
    class UpdateAuthor{
        @Test
        @DisplayName("Should update author when valid data is provided")
        void shouldUpdateAuthorWhenValidDataIsProvided() throws Exception {
            Long authorId = 1L;
            CreateAuthorDTO createAuthorDto = CreateAuthorDTO.builder()
                    .name("John Updated")
                    .nationality("Canadian")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("Updated biography")
                    .build();
            Author updatedAuthor = Author.builder()
                    .id(authorId)
                    .name("John Updated")
                    .nationality("Canadian")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("Updated biography")
                    .build();

            when(authorService.updateAuthor(any(CreateAuthorDTO.class),eq(authorId))).thenReturn(updatedAuthor);

            mockMvc.perform(put("/api/authors/{id}", authorId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createAuthorDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(authorId));

            verify(authorService).updateAuthor(any(CreateAuthorDTO.class),eq(authorId));
        }
        @Test
        @DisplayName("Should return 400 when invalid data is provided")
        void shouldReturn400WhenInvalidDataIsProvided() throws Exception{
            Long authorId = 1L;
            CreateAuthorDTO invalidCreateAuthorDTO = new CreateAuthorDTO();

            mockMvc.perform(put("/api/authors/{id}", authorId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCreateAuthorDTO)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Should return 404 when trying to update non-existent author")
        void shouldReturn404WhenTryingToUpdateNonExistentAuthor() throws Exception {
            Long nonExistingAuthorId = 999L;
            CreateAuthorDTO createAuthorDto = CreateAuthorDTO.builder()
                    .name("John Updated")
                    .nationality("Canadian")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("Updated biography")
                    .build();

            when(authorService.updateAuthor(any(CreateAuthorDTO.class),eq(nonExistingAuthorId))).thenThrow(AuthorNotFoundException.class);

            mockMvc.perform(put("/api/authors/{id}", nonExistingAuthorId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createAuthorDto)))
                    .andExpect(status().isNotFound());
        }
        @Test
        @DisplayName("Should return 400 when invalid ID format is provided")
        void shouldReturn400WhenInvalidIdFormatIsProvided() throws Exception {
            String invalidId = "invalid";
            CreateAuthorDTO createAuthorDto = CreateAuthorDTO.builder()
                    .name("John Updated")
                    .nationality("Canadian")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Author.Gender.MALE)
                    .biography("Updated biography")
                    .build();

            mockMvc.perform(put("/api/authors/{id}", invalidId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createAuthorDto)))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("DELETE /api/authors/{id} - Delete Author")
    class DeleteAuthor {

        @Test
        @DisplayName("Should delete author when valid ID is provided")
        void shouldDeleteAuthorWhenValidIdIsProvided() throws Exception {
            Long authorId = 1L;

            doNothing().when(authorService).deleteAuthorById(authorId);

            mockMvc.perform(delete("/api/authors/{id}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .andExpect(content().string("Author Deleted Successfully"));
        }
        @Test
        @DisplayName("Should return 404 when trying to delete non-existent author")
        void shouldReturn404WhenTryingToDeleteNonExistentAuthor() throws Exception {
            Long nonExistingAuthorId = 999L;

            doThrow(AuthorNotFoundException.class).when(authorService).deleteAuthorById(nonExistingAuthorId);


            mockMvc.perform(delete("/api/authors/{id}", nonExistingAuthorId))
                    .andExpect(status().isNotFound());
        }
        @Test
        @DisplayName("Should return 400 when invalid ID format is provided")
        void shouldReturn400WhenInvalidIdFormatIsProvided() throws Exception {
            String invalidId = "invalid";

            mockMvc.perform(delete("/api/authors/{id}", invalidId))
                    .andExpect(status().isBadRequest());
        }

    }
}

