package com.bookstore.management.books.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    private boolean allowIsbn10;
    private boolean allowIsbn13;

    @Override
    public void initialize(ValidIsbn constraintAnnotation) {
        this.allowIsbn10 = constraintAnnotation.allosIsbn10();
        this.allowIsbn13 = constraintAnnotation.allosIsbn13();
    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        String normalizedIsbn = isbn.replaceAll("[\\s-]", "").toUpperCase();

        if (allowIsbn10 && normalizedIsbn.length() == 10) {
            return isValidIsbn10(normalizedIsbn);
        }
        if (allowIsbn13 && normalizedIsbn.length() == 13) {
            return isValidIsbn13(normalizedIsbn);
        }
        return false;
    }

    private boolean isValidIsbn13(String isbn) {
        if (!isbn.matches("^\\d{13}$")) {
            return false;
        }

        if (!isbn.startsWith("978") && !isbn.startsWith("979")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int checkDigit = Character.getNumericValue(isbn.charAt(12));
        int calculatedCheck = (10 - (sum % 10)) % 10;

        return checkDigit == calculatedCheck;

    }

    private boolean isValidIsbn10(String isbn) {

        if (!isbn.matches("^\\d{9}[\\dXx]$")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(isbn.charAt(i)) * (10 - i);
        }

        char checkDigit = isbn.charAt(9);
        int checkValue = (checkDigit == 'X' || checkDigit == 'x') ? 10 : Character.getNumericValue(checkDigit);

        return (sum + checkValue) % 11 == 0;
    }
}
