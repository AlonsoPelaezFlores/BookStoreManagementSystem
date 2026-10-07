package com.bookstore.management.user.controller;

import com.bookstore.management.security.CustomUserDetailService;
import com.bookstore.management.security.JwtUtils;
import com.bookstore.management.shared.exception.custom.DuplicateEntityException;
import com.bookstore.management.user.dto.AuthResponseDTO;
import com.bookstore.management.user.dto.LoginRequestDTO;
import com.bookstore.management.user.dto.RegisterRequestDTO;
import com.bookstore.management.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Auth Controller Test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private CustomUserDetailService userDetailService;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("Should return 201 with a token when registration succeeds")
        void shouldReturn201WithTokenWhenRegistrationSucceeds() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("new.employee@bookstore.com")
                    .password("plainPassword123")
                    .build();

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token("jwt-token")
                    .expiresInMillis(86_400_000L)
                    .build();

            when(authService.register(any(RegisterRequestDTO.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").value("jwt-token"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresInMillis").value(86_400_000L));
        }

        @Test
        @DisplayName("Should return 400 when the email is blank")
        void shouldReturn400WhenEmailIsBlank() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("")
                    .password("plainPassword123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when the email is not a valid format")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("not-an-email")
                    .password("plainPassword123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when the password is shorter than 8 characters")
        void shouldReturn400WhenPasswordTooShort() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("new.employee@bookstore.com")
                    .password("short")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when the email is already registered")
        void shouldReturn409WhenEmailAlreadyRegistered() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("existing@bookstore.com")
                    .password("plainPassword123")
                    .build();

            when(authService.register(any(RegisterRequestDTO.class)))
                    .thenThrow(new DuplicateEntityException("User", "email", "existing@bookstore.com"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("Should return 200 with a token when credentials are valid")
        void shouldReturn200WithTokenWhenCredentialsAreValid() throws Exception {
            LoginRequestDTO request = LoginRequestDTO.builder()
                    .email("admin@bookstore.com")
                    .password("plainPassword123")
                    .build();

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token("jwt-token")
                    .expiresInMillis(86_400_000L)
                    .build();

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token"));
        }

        @Test
        @DisplayName("Should return 401 when credentials are invalid")
        void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
            LoginRequestDTO request = LoginRequestDTO.builder()
                    .email("admin@bookstore.com")
                    .password("wrong-password")
                    .build();

            when(authService.login(any(LoginRequestDTO.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 400 when the request body is missing required fields")
        void shouldReturn400WhenBodyMissingFields() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
