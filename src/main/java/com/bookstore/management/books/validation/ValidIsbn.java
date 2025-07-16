package com.bookstore.management.books.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {IsbnValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIsbn {
    String message() default "Invalid ISBN. Should be an ISBN-10 or ISBN-13";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    boolean allowIsbn10() default true;
    boolean allowIsbn13() default true;
}
