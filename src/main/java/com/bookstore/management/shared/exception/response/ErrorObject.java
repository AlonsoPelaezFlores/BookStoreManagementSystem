package com.bookstore.management.shared.exception.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorObject {

    private final LocalDateTime timestamp;
    private final String code;
    private final Integer status;
    private final String message;
    private final String path;
}
