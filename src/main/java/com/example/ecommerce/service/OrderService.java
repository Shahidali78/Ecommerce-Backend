package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles placing orders from carts. Converts cart items into immutable order
 * items, saves the order and clears the cart. Payment integration would
 * typically happen outside this service, for example via a PaymentService.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
    }

    public Order placeOrder(User user) {
        Cart cart = cartService.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("User has no cart"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        Order order = new Order();
        order.setUser(user);
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(cartItem.getProduct());
            oi.setQuantity(cartItem.getQuantity());
            oi.setPrice(cartItem.getUnitPrice());
            orderItems.add(oi);
            total = total.add(cartItem.getTotalPrice());
        }
        order.setItems(orderItems);
        order.setTotalPrice(total);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.PENDING);
        orderRepository.save(order);
        for (OrderItem oi : orderItems) {
            orderItemRepository.save(oi);
        }
        cartService.clearCart(cart);
        return order;
    }
}