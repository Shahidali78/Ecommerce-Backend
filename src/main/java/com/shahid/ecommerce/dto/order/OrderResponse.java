package com.shahid.ecommerce.dto.order;

import com.shahid.ecommerce.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String customerName,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        OrderStatus status,
        String shippingAddress,
        Instant createdAt,
        Instant updatedAt
) {
}
