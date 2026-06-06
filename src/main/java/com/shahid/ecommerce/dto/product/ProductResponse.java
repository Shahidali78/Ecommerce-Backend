package com.shahid.ecommerce.dto.product;

import com.shahid.ecommerce.dto.category.CategoryResponse;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        boolean active,
        CategoryResponse category,
        Instant createdAt,
        Instant updatedAt
) {
}
