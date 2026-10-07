package com.bookstore.management.user.service;

import com.bookstore.management.security.JwtUtils;
import com.bookstore.management.shared.exception.custom.DuplicateEntityException;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import com.bookstore.management.user.dto.AuthResponseDTO;
import com.bookstore.management.user.dto.LoginRequestDTO;
import com.bookstore.management.user.dto.RegisterRequestDTO;
import com.bookstore.management.user.entity.Role;
import com.bookstore.management.user.entity.User;
import com.bookstore.management.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Test")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("Register")
    class Register {

        @Test
        @DisplayName("Should create an EMPLOYEE with an encoded password and return a token")
        void shouldCreateEmployeeWithEncodedPasswordAndReturnToken() {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("new.employee@bookstore.com")
                    .password("plainPassword123")
                    .build();

            when(userRepository.findByEmail("new.employee@bookstore.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("plainPassword123")).thenReturn("encoded-hash");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(jwtUtils.generateToken(any())).thenReturn("jwt-token");
            when(jwtUtils.getExpirationMillis()).thenReturn(86_400_000L);

            AuthResponseDTO result = authService.register(request);

            assertThat(result.getToken()).isEqualTo("jwt-token");
            assertThat(result.getTokenType()).isEqualTo("Bearer");
            assertThat(result.getExpiresInMillis()).isEqualTo(86_400_000L);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getEmail()).isEqualTo("new.employee@bookstore.com");
            assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("encoded-hash");
            assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Should throw when the email is already registered")
        void shouldThrowWhenEmailAlreadyRegistered() {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("existing@bookstore.com")
                    .password("plainPassword123")
                    .build();

            User existing = User.builder().id(1L).email("existing@bookstore.com").role(Role.EMPLOYEE).build();
            when(userRepository.findByEmail("existing@bookstore.com")).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(DuplicateEntityException.class)
                    .hasMessageContaining("existing@bookstore.com");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login")
    class Login {

        @Test
        @DisplayName("Should authenticate and return a token when credentials are valid")
        void shouldAuthenticateAndReturnTokenWhenCredentialsAreValid() {
            LoginRequestDTO request = LoginRequestDTO.builder()
                    .email("admin@bookstore.com")
                    .password("plainPassword123")
                    .build();

            User user = User.builder()
                    .id(1L)
                    .email("admin@bookstore.com")
                    .passwordHash("hash")
                    .role(Role.ADMIN)
                    .build();

            when(userRepository.findByEmail("admin@bookstore.com")).thenReturn(Optional.of(user));
            when(jwtUtils.generateToken(user)).thenReturn("jwt-token");
            when(jwtUtils.getExpirationMillis()).thenReturn(86_400_000L);

            AuthResponseDTO result = authService.login(request);

            assertThat(result.getToken()).isEqualTo("jwt-token");

            ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(authCaptor.capture());
            assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("admin@bookstore.com");
            assertThat(authCaptor.getValue().getCredentials()).isEqualTo("plainPassword123");
        }

        @Test
        @DisplayName("Should propagate the failure when credentials are invalid")
        void shouldPropagateFailureWhenCredentialsAreInvalid() {
            LoginRequestDTO request = LoginRequestDTO.builder()
                    .email("admin@bookstore.com")
                    .password("wrong-password")
                    .build();

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);

            verify(userRepository, never()).findByEmail(any());
        }

        @Test
        @DisplayName("Should throw when the authenticated user can no longer be found")
        void shouldThrowWhenAuthenticatedUserCanNoLongerBeFound() {
            LoginRequestDTO request = LoginRequestDTO.builder()
                    .email("ghost@bookstore.com")
                    .password("plainPassword123")
                    .build();

            when(userRepository.findByEmail("ghost@bookstore.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("ghost@bookstore.com");
        }
    }
}
