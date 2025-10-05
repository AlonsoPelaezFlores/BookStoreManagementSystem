package com.bookstore.management.book.validation;


import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ISBN Validator Tests")
class IsbnTest {

    @Nested
    @DisplayName("ISBN Validator tests")
    class ValidatorTest{
        private IsbnValidator validator;

        @Mock
        private ValidIsbn validIsbn;
        @Mock
        private ConstraintValidatorContext context;
        @BeforeEach
        void setUp() {
            validator = new IsbnValidator();
            when(validIsbn.allowIsbn10()).thenReturn(true);
            when(validIsbn.allowIsbn13()).thenReturn(true);
            validator.initialize(validIsbn);
        }

        @Test
        @DisplayName("Should validate valid ISBN-10  ")
        void shouldValidateValidIsbn10() {
            assertTrue(validator.isValid("0596520689", context));
            assertTrue(validator.isValid("0-596-52068-9", context));
            assertTrue(validator.isValid("0 596 52068 9", context));
            assertTrue(validator.isValid("0136012671", context));
            assertTrue(validator.isValid("155860832X", context));
        }

        @Test
        @DisplayName("Should validate valid ISBN-13")
        void shouldValidateValidIsbn13() {
            assertTrue(validator.isValid("9780596520687", context));
            assertTrue(validator.isValid("978-0-596-52068-7", context));
            assertTrue(validator.isValid("978 0 596 52068 7", context));
            assertTrue(validator.isValid("9791234567896", context));
        }
        @Test
        @DisplayName("Should reject invalid ISBN-10")
        void shouldRejectInvalidISBN10() {
            assertFalse(validator.isValid("0596520688", context));
            assertFalse(validator.isValid("059652068A", context));
            assertFalse(validator.isValid("05965206", context));
            assertFalse(validator.isValid("05965206890", context));
        }
        @Test
        @DisplayName("Should reject invalid ISBN-13")
        void shouldRejectInvalidIsbn13() {
            assertFalse(validator.isValid("9780596520686", context));
            assertFalse(validator.isValid("1234567890123", context));
            assertFalse(validator.isValid("978059652068", context));
            assertFalse(validator.isValid("97805965206870", context));
        }
        @Test
        @DisplayName("Should reject null and empty values")
        void shouldRejectNullAndEmpty() {
            assertFalse(validator.isValid(null, context));
            assertFalse(validator.isValid("", context));
            assertFalse(validator.isValid("   ", context));
        }
        @Test
        @DisplayName("Should respect configuration for ISBN-10 only")
        void shouldRespectIsbn10OnlyConfiguration() {
            when(validIsbn.allowIsbn10()).thenReturn(true);
            when(validIsbn.allowIsbn13()).thenReturn(false);
            validator.initialize(validIsbn);

            assertTrue(validator.isValid("0596520689", context));
            assertFalse(validator.isValid("9780596520687", context));
        }
        @Test
        @DisplayName("Should respect configuration for ISBN-13 only")
        void shouldRespectIsbn13OnlyConfiguration() {
            when(validIsbn.allowIsbn10()).thenReturn(false);
            when(validIsbn.allowIsbn13()).thenReturn(true);
            validator.initialize(validIsbn);

            assertFalse(validator.isValid("0596520689", context));
            assertTrue(validator.isValid("9780596520687", context));
        }
    }

    @Nested
    @DisplayName("ISBN Integration tests")
    class IntegrationTest{

        private Validator validator;

        @BeforeEach
        void setUp() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        @DisplayName("Should validate entity with valid isbn")
        void shouldValidateEntityWithValidIsbn() {
            TestEntity entity = new TestEntity("9780596520687");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should reject entity with invalid isbn")
        void shouldRejectEntityWithInvalidIsbn() {
            TestEntity entity = new TestEntity("invalid-isbn");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

            assertEquals(1, violations.size());
            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("isbn", violation.getPropertyPath().toString());
            assertTrue(violation.getMessage().contains("Invalid ISBN"));
        }

        static class TestEntity {
            @ValidIsbn
            private String isbn;

            public TestEntity(String isbn) {
                this.isbn = isbn;
            }
        }
    }
}

