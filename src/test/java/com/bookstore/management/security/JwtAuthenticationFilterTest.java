package com.bookstore.management.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Jwt Authentication Filter Test")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private CustomUserDetailService userDetailService;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtils, userDetailService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("No Bearer Token")
    class NoBearerToken {

        @Test
        @DisplayName("Should continue the chain without authenticating when the header is missing")
        void shouldContinueChainWithoutAuthenticatingWhenHeaderMissing() throws Exception {
            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(jwtUtils, userDetailService);
        }

        @Test
        @DisplayName("Should continue the chain without authenticating when the header isn't a Bearer token")
        void shouldContinueChainWithoutAuthenticatingWhenHeaderIsNotBearer() throws Exception {
            request.addHeader("Authorization", "Basic abc123");

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(jwtUtils, userDetailService);
        }
    }

    @Nested
    @DisplayName("Valid Bearer Token")
    class ValidBearerToken {

        @Test
        @DisplayName("Should authenticate the user and continue the chain")
        void shouldAuthenticateUserAndContinueChain() throws Exception {
            UserDetails userDetails = User.withUsername("employee@bookstore.com")
                    .password("hash")
                    .roles("EMPLOYEE")
                    .build();

            request.addHeader("Authorization", "Bearer valid-token");
            when(jwtUtils.extractUsername("valid-token")).thenReturn("employee@bookstore.com");
            when(userDetailService.loadUserByUsername("employee@bookstore.com")).thenReturn(userDetails);
            when(jwtUtils.isTokenValid("valid-token", userDetails)).thenReturn(true);

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should not authenticate when the token is no longer valid (e.g. expired)")
        void shouldNotAuthenticateWhenTokenIsNoLongerValid() throws Exception {
            UserDetails userDetails = User.withUsername("employee@bookstore.com")
                    .password("hash")
                    .roles("EMPLOYEE")
                    .build();

            request.addHeader("Authorization", "Bearer stale-token");
            when(jwtUtils.extractUsername("stale-token")).thenReturn("employee@bookstore.com");
            when(userDetailService.loadUserByUsername("employee@bookstore.com")).thenReturn(userDetails);
            when(jwtUtils.isTokenValid("stale-token", userDetails)).thenReturn(false);

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should not query the user details service again when already authenticated")
        void shouldNotReauthenticateWhenAlreadyAuthenticated() throws Exception {
            UsernamePasswordAuthenticationToken existingAuth =
                    new UsernamePasswordAuthenticationToken("someone", null, List.of());
            SecurityContextHolder.getContext().setAuthentication(existingAuth);

            request.addHeader("Authorization", "Bearer valid-token");

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(jwtUtils, userDetailService);
        }
    }

    @Nested
    @DisplayName("Invalid Bearer Token")
    class InvalidBearerToken {

        @Test
        @DisplayName("Should continue the chain without authenticating when the token is malformed/expired")
        void shouldContinueChainWhenTokenThrowsJwtException() throws Exception {
            request.addHeader("Authorization", "Bearer garbage-token");
            when(jwtUtils.extractUsername("garbage-token")).thenThrow(new JwtException("invalid"));

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should continue the chain without authenticating when the user no longer exists")
        void shouldContinueChainWhenUserNoLongerExists() throws Exception {
            request.addHeader("Authorization", "Bearer valid-token");
            when(jwtUtils.extractUsername("valid-token")).thenReturn("ghost@bookstore.com");
            when(userDetailService.loadUserByUsername("ghost@bookstore.com"))
                    .thenThrow(new UsernameNotFoundException("not found"));

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }
}
