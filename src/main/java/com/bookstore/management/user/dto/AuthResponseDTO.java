package com.bookstore.management.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private long expiresInMillis;
}
