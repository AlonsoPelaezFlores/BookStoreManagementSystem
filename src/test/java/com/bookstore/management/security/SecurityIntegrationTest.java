package com.bookstore.management.security;

import com.bookstore.management.inventory.dto.UpdateStockDTO;
import com.bookstore.management.inventory.model.MovementType;
import jakarta.persistence.EntityManager;
import com.bookstore.management.user.dto.LoginRequestDTO;
import com.bookstore.management.user.dto.RegisterRequestDTO;
import com.bookstore.management.user.entity.Role;
import com.bookstore.management.user.entity.User;
import com.bookstore.management.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end verification of the real security filter chain (no mocked security beans):
 * SecurityConfig, JwtAuthenticationFilter, JwtUtils, CustomUserDetailService,
 * AuthController/AuthService and the GlobalExceptionHandler/JwtAuthenticationEntryPoint
 * error responses, running against an in-memory H2 database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Security Integration Test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private EntityManager entityManager;

    /**
     * Saves the user, then flushes and clears the persistence context so a
     * subsequent lookup (e.g. via the login endpoint) is forced to re-hydrate
     * the entity from a fresh query instead of returning the same in-memory
     * instance from Hibernate's first-level cache. Without this, a missing
     * no-arg constructor on the entity (required for Hibernate to instantiate
     * it via reflection) would go completely undetected by this test.
     */
    private User persistAndDetach(User user) {
        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        return saved;
    }

    @Nested
    @DisplayName("Public Endpoints")
    class PublicEndpoints {

        @Test
        @DisplayName("Should allow registration without a token")
        void shouldAllowRegistrationWithoutToken() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("integration.employee@bookstore.com")
                    .password("plainPassword123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Should reject a second registration with the same email")
        void shouldRejectDuplicateRegistration() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("duplicate@bookstore.com")
                    .password("plainPassword123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Protected Endpoints Without A Valid Token")
    class ProtectedEndpointsWithoutValidToken {

        @Test
        @DisplayName("Should reject with 401 when no Authorization header is sent")
        void shouldReject401WhenNoHeaderSent() throws Exception {
            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401));
        }

        @Test
        @DisplayName("Should reject with 401 when the token is garbage")
        void shouldReject401WhenTokenIsGarbage() throws Exception {
            mockMvc.perform(get("/api/books").header("Authorization", "Bearer not-a-real-token"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Full Register/Login/Access Flow")
    class FullFlow {

        @Test
        @DisplayName("Should access a protected endpoint using the token returned at registration")
        void shouldAccessProtectedEndpointUsingRegistrationToken() throws Exception {
            RegisterRequestDTO request = RegisterRequestDTO.builder()
                    .email("flow.employee@bookstore.com")
                    .password("plainPassword123")
                    .build();

            String response = mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            String token = objectMapper.readTree(response).get("token").asText();

            mockMvc.perform(get("/api/books").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should log in an existing user and access a protected endpoint with the returned token")
        void shouldLoginAndAccessProtectedEndpoint() throws Exception {
            User user = User.builder()
                    .email("existing.employee@bookstore.com")
                    .passwordHash(passwordEncoder.encode("plainPassword123"))
                    .role(Role.EMPLOYEE)
                    .build();
            persistAndDetach(user);

            LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                    .email("existing.employee@bookstore.com")
                    .password("plainPassword123")
                    .build();

            String response = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode json = objectMapper.readTree(response);
            String token = json.get("token").asText();

            mockMvc.perform(get("/api/books").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should reject login with the wrong password")
        void shouldRejectLoginWithWrongPassword() throws Exception {
            User user = User.builder()
                    .email("wrongpass.employee@bookstore.com")
                    .passwordHash(passwordEncoder.encode("correctPassword123"))
                    .role(Role.EMPLOYEE)
                    .build();
            persistAndDetach(user);

            LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                    .email("wrongpass.employee@bookstore.com")
                    .password("wrongPassword")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    /**
     * These endpoints are restricted with {@code @PreAuthorize("hasRole('ADMIN')")}. A 403 for
     * EMPLOYEE proves the restriction actually runs; using a non-existent id for the ADMIN case
     * proves the permission check was passed (execution reached the service, which then reports
     * 404) without needing to fabricate the underlying book/author/customer/inventory records.
     */
    @Nested
    @DisplayName("Role-Based Authorization On Admin-Only Endpoints")
    class RoleBasedAuthorization {

        private static final long NON_EXISTENT_ID = 999_999L;

        private String adminToken;
        private String employeeToken;

        @BeforeEach
        void setUp() {
            User admin = persistAndDetach(User.builder()
                    .email("role.admin@bookstore.com")
                    .passwordHash(passwordEncoder.encode("irrelevant"))
                    .role(Role.ADMIN)
                    .build());
            User employee = persistAndDetach(User.builder()
                    .email("role.employee@bookstore.com")
                    .passwordHash(passwordEncoder.encode("irrelevant"))
                    .role(Role.EMPLOYEE)
                    .build());

            adminToken = jwtUtils.generateToken(admin);
            employeeToken = jwtUtils.generateToken(employee);
        }

        @Test
        @DisplayName("Deleting a book is ADMIN-only")
        void deleteBookIsAdminOnly() throws Exception {
            mockMvc.perform(delete("/api/books/{id}", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete("/api/books/{id}", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deleting an author is ADMIN-only")
        void deleteAuthorIsAdminOnly() throws Exception {
            mockMvc.perform(delete("/api/authors/{id}", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete("/api/authors/{id}", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deleting a customer is ADMIN-only")
        void deleteCustomerIsAdminOnly() throws Exception {
            mockMvc.perform(delete("/api/customers/{id}", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete("/api/customers/{id}", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Disabling an inventory record is ADMIN-only")
        void disableInventoryIsAdminOnly() throws Exception {
            mockMvc.perform(patch("/api/inventory/{id}/disable", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(patch("/api/inventory/{id}/disable", NON_EXISTENT_ID)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Updating stock thresholds is ADMIN-only")
        void updateThresholdsIsAdminOnly() throws Exception {
            mockMvc.perform(patch("/api/inventory/book/{bookId}/thresholds", NON_EXISTENT_ID)
                            .param("stockMin", "1")
                            .param("stockMax", "10")
                            .header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(patch("/api/inventory/book/{bookId}/thresholds", NON_EXISTENT_ID)
                            .param("stockMin", "1")
                            .param("stockMax", "10")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("A positive stock adjustment is ADMIN-only")
        void positiveAdjustmentIsAdminOnly() throws Exception {
            String body = objectMapper.writeValueAsString(
                    new UpdateStockDTO(5, MovementType.POSITIVE_ADJUSTMENT));

            mockMvc.perform(post("/api/inventory/book/{bookId}/adjustment/positive", NON_EXISTENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/inventory/book/{bookId}/adjustment/positive", NON_EXISTENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("A negative stock adjustment is ADMIN-only")
        void negativeAdjustmentIsAdminOnly() throws Exception {
            String body = objectMapper.writeValueAsString(
                    new UpdateStockDTO(5, MovementType.NEGATIVE_ADJUSTMENT));

            mockMvc.perform(post("/api/inventory/book/{bookId}/adjustment/negative", NON_EXISTENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/inventory/book/{bookId}/adjustment/negative", NON_EXISTENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("EMPLOYEE can still access endpoints that aren't ADMIN-only")
        void employeeCanAccessNonAdminOnlyEndpoints() throws Exception {
            mockMvc.perform(get("/api/books").header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/inventory").header("Authorization", "Bearer " + employeeToken))
                    .andExpect(status().isOk());
        }
    }
}
