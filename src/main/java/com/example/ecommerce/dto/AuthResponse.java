package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response body returned to the client on successful authentication. It
 * contains the JWT token that must be included on subsequent requests.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
}