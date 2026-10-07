package com.bookstore.management.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Jwt Utils Test")
class JwtUtilsTest {

    private static final String SECRET = "unit-test-only-jwt-secret-key-1234567890-abcdefghijklmnopqrstuvwxyz";
    private static final String OTHER_SECRET = "a-completely-different-unit-test-secret-key-0987654321-zyxwvutsrqponml";
    private static final long EXPIRATION_MILLIS = 3_600_000L;

    private JwtUtils jwtUtils;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = newJwtUtils(SECRET, EXPIRATION_MILLIS);

        userDetails = User.withUsername("employee@bookstore.com")
                .password("irrelevant")
                .roles("EMPLOYEE")
                .build();
    }

    private JwtUtils newJwtUtils(String secret, long expirationMillis) {
        JwtUtils utils = new JwtUtils();
        ReflectionTestUtils.setField(utils, "secret", secret);
        ReflectionTestUtils.setField(utils, "jwtExpiration", expirationMillis);
        return utils;
    }

    @Nested
    @DisplayName("Generate Token")
    class GenerateToken {

        @Test
        @DisplayName("Should generate a non-blank token for a valid user")
        void shouldGenerateNonBlankTokenForValidUser() {
            String token = jwtUtils.generateToken(userDetails);

            assertThat(token).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Extract Username")
    class ExtractUsername {

        @Test
        @DisplayName("Should extract the username used to generate the token")
        void shouldExtractUsernameUsedToGenerateToken() {
            String token = jwtUtils.generateToken(userDetails);

            assertThat(jwtUtils.extractUsername(token)).isEqualTo(userDetails.getUsername());
        }

        @Test
        @DisplayName("Should throw when the token is malformed")
        void shouldThrowWhenTokenIsMalformed() {
            assertThatThrownBy(() -> jwtUtils.extractUsername("not-a-real-token"))
                    .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("Should throw when the token was signed with a different key")
        void shouldThrowWhenTokenSignedWithDifferentKey() {
            String token = jwtUtils.generateToken(userDetails);
            JwtUtils otherJwtUtils = newJwtUtils(OTHER_SECRET, EXPIRATION_MILLIS);

            assertThatThrownBy(() -> otherJwtUtils.extractUsername(token))
                    .isInstanceOf(JwtException.class);
        }
    }

    @Nested
    @DisplayName("Is Token Valid")
    class IsTokenValid {

        @Test
        @DisplayName("Should return true for a matching, non-expired token")
        void shouldReturnTrueForMatchingNonExpiredToken() {
            String token = jwtUtils.generateToken(userDetails);

            assertThat(jwtUtils.isTokenValid(token, userDetails)).isTrue();
        }

        @Test
        @DisplayName("Should return false when the token belongs to a different user")
        void shouldReturnFalseWhenTokenBelongsToDifferentUser() {
            String token = jwtUtils.generateToken(userDetails);
            UserDetails anotherUser = User.withUsername("someone-else@bookstore.com")
                    .password("irrelevant")
                    .roles("EMPLOYEE")
                    .build();

            assertThat(jwtUtils.isTokenValid(token, anotherUser)).isFalse();
        }

        @Test
        @DisplayName("Should throw when the token is already expired")
        void shouldThrowWhenTokenIsAlreadyExpired() {
            JwtUtils expiredJwtUtils = newJwtUtils(SECRET, -1000L);
            String expiredToken = expiredJwtUtils.generateToken(userDetails);

            assertThatThrownBy(() -> expiredJwtUtils.isTokenValid(expiredToken, userDetails))
                    .isInstanceOf(JwtException.class);
        }
    }

    @Nested
    @DisplayName("Get Expiration Millis")
    class GetExpirationMillis {

        @Test
        @DisplayName("Should return the configured expiration")
        void shouldReturnConfiguredExpiration() {
            assertThat(jwtUtils.getExpirationMillis()).isEqualTo(EXPIRATION_MILLIS);
        }
    }
}
