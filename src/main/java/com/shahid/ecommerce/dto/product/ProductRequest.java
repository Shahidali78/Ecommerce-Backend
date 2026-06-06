package com.shahid.ecommerce.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 2000) String description,
        @NotNull @Positive BigDecimal price,
        @NotNull @PositiveOrZero Integer stock,
        @Size(max = 1000) String imageUrl,
        @NotNull Long categoryId
) {
}
