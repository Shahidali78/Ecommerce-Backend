package com.shahid.ecommerce.dto.order;

import com.shahid.ecommerce.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status
) {
}
