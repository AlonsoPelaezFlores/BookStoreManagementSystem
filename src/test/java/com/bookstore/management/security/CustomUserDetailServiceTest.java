package com.bookstore.management.security;

import com.bookstore.management.user.entity.Role;
import com.bookstore.management.user.entity.User;
import com.bookstore.management.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Custom User Detail Service Test")
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Nested
    @DisplayName("Load User By Username")
    class LoadUserByUsername {

        @Test
        @DisplayName("Should return the user when the email exists")
        void shouldReturnUserWhenEmailExists() {
            User user = User.builder()
                    .id(1L)
                    .email("admin@bookstore.com")
                    .passwordHash("hashed")
                    .role(Role.ADMIN)
                    .build();

            when(userRepository.findByEmail("admin@bookstore.com")).thenReturn(Optional.of(user));

            UserDetails result = customUserDetailService.loadUserByUsername("admin@bookstore.com");

            assertThat(result.getUsername()).isEqualTo("admin@bookstore.com");
            assertThat(result.getPassword()).isEqualTo("hashed");
            assertThat(result.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
            verify(userRepository).findByEmail("admin@bookstore.com");
        }

        @Test
        @DisplayName("Should throw when the email does not exist")
        void shouldThrowWhenEmailDoesNotExist() {
            when(userRepository.findByEmail("missing@bookstore.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customUserDetailService.loadUserByUsername("missing@bookstore.com"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("missing@bookstore.com");

            verify(userRepository).findByEmail("missing@bookstore.com");
        }
    }
}
