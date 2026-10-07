package com.bookstore.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Custom Access Denied Handler Test")
class CustomAccessDeniedHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private CustomAccessDeniedHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CustomAccessDeniedHandler(objectMapper);
    }

    @Test
    @DisplayName("Should write a 403 JSON error body")
    void shouldWrite403JsonErrorBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/books/1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("denied"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo("application/json");

        Map<?, ?> body = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(body.get("status")).isEqualTo(403);
        assertThat(body.get("path")).isEqualTo("/api/books/1");
        assertThat(body.get("message")).isEqualTo("You do not have permission to access this resource");
    }
}
