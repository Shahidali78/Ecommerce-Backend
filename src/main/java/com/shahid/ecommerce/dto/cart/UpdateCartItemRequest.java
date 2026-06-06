package com.shahid.ecommerce.dto.cart;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(
        @Positive int quantity
) {
}
