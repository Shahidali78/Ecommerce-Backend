package com.shahid.ecommerce.service.impl;

import com.shahid.ecommerce.dto.order.CreateOrderRequest;
import com.shahid.ecommerce.dto.order.OrderResponse;
import com.shahid.ecommerce.exception.BadRequestException;
import com.shahid.ecommerce.exception.ResourceNotFoundException;
import com.shahid.ecommerce.model.AppUser;
import com.shahid.ecommerce.model.CartItem;
import com.shahid.ecommerce.model.CustomerOrder;
import com.shahid.ecommerce.model.OrderItem;
import com.shahid.ecommerce.model.OrderStatus;
import com.shahid.ecommerce.model.Product;
import com.shahid.ecommerce.repository.CartItemRepository;
import com.shahid.ecommerce.repository.OrderRepository;
import com.shahid.ecommerce.repository.ProductRepository;
import com.shahid.ecommerce.service.CurrentUserService;
import com.shahid.ecommerce.service.DtoMapper;
import com.shahid.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
            OrderStatus.PROCESSING, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class)
    );

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            CurrentUserService currentUserService
    ) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        AppUser user = currentUserService.requireCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByIdAsc(user.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot place an order with an empty cart");
        }

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.shippingAddress().trim());

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("A product in the cart no longer exists"));
            if (!product.isActive()) {
                throw new BadRequestException(product.getName() + " is no longer available");
            }
            if (cartItem.getQuantity() > product.getStock()) {
                throw new BadRequestException(
                        "Only " + product.getStock() + " unit(s) of " + product.getName() + " are available"
                );
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(subtotal);
            order.addItem(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);
        CustomerOrder saved = orderRepository.save(order);
        cartItemRepository.deleteAllByUserId(user.getId());
        return DtoMapper.toOrder(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> findMyOrders(Pageable pageable) {
        AppUser user = currentUserService.requireCurrentUser();
        return orderRepository.findAllByUserId(user.getId(), pageable).map(DtoMapper::toOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(DtoMapper::toOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        CustomerOrder order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == status) {
            return DtoMapper.toOrder(order);
        }
        if (!ALLOWED_TRANSITIONS.get(order.getStatus()).contains(status)) {
            throw new BadRequestException(
                    "Order cannot move from " + order.getStatus() + " to " + status
            );
        }
        if (status == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(status);
        return DtoMapper.toOrder(orderRepository.save(order));
    }

    private void restoreStock(CustomerOrder order) {
        for (OrderItem item : order.getItems()) {
            productRepository.findByIdForUpdate(item.getProductId())
                    .ifPresent(product -> product.setStock(product.getStock() + item.getQuantity()));
        }
    }
}
