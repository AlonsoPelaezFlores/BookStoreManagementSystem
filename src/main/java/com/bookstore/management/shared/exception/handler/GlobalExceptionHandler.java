package com.bookstore.management.shared.exception.handler;

import com.bookstore.management.shared.exception.response.ErrorObject;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorObject> handleResourceNotFound(ResourceNotFoundException ex,
                                                              HttpServletRequest request ) {
        log.warn("Resource not found: {} at path: {}", ex.getMessage(), request.getRequestURI());
        ErrorObject.ErrorObjectBuilder errorObjectBuilder =
                ErrorObject.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI());
        return new ResponseEntity<>(errorObjectBuilder.build(), HttpStatus.NOT_FOUND);
    }
}
