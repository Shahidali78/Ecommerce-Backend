package com.example.ecommerce.dto;

import lombok.Data;

/**
 * Request body for authentication. Contains the username/email and password
 * submitted by the user. Spring automatically binds JSON to this object.
 */
@Data
public class AuthRequest {
    private String email;
    private String password;
}