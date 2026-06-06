package com.shahid.ecommerce.dto.user;

import com.shahid.ecommerce.model.Role;

import java.time.Instant;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String address,
        Role role,
        Instant createdAt
) {
}
