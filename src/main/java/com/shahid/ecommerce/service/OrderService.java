package com.shahid.ecommerce.service;

import com.shahid.ecommerce.dto.order.CreateOrderRequest;
import com.shahid.ecommerce.dto.order.OrderResponse;
import com.shahid.ecommerce.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request);

    Page<OrderResponse> findMyOrders(Pageable pageable);

    Page<OrderResponse> findAll(Pageable pageable);

    OrderResponse updateStatus(Long id, OrderStatus status);
}
