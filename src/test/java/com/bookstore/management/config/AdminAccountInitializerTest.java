package com.bookstore.management.config;

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
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Account Initializer Test")
class AdminAccountInitializerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private AdminAccountInitializer initializer;

    @Nested
    @DisplayName("Run")
    class Run {

        @Test
        @DisplayName("Should do nothing when an ADMIN already exists")
        void shouldDoNothingWhenAdminAlreadyExists() {
            when(userRepository.existsByRole(Role.ADMIN)).thenReturn(true);

            initializer.run(applicationArguments);

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should skip bootstrapping when no admin credentials are configured")
        void shouldSkipWhenNoCredentialsConfigured() {
            when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);
            ReflectionTestUtils.setField(initializer, "adminEmail", "");
            ReflectionTestUtils.setField(initializer, "adminPassword", "");

            initializer.run(applicationArguments);

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should create the first ADMIN when credentials are configured")
        void shouldCreateFirstAdminWhenCredentialsConfigured() {
            when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);
            ReflectionTestUtils.setField(initializer, "adminEmail", "admin@bookstore.com");
            ReflectionTestUtils.setField(initializer, "adminPassword", "SuperSecret123!");
            when(passwordEncoder.encode("SuperSecret123!")).thenReturn("encoded-hash");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            initializer.run(applicationArguments);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo("admin@bookstore.com");
            assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded-hash");
            assertThat(captor.getValue().getRole()).isEqualTo(Role.ADMIN);
        }
    }
}
