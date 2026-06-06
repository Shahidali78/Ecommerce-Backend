package com.shahid.ecommerce.service;

import com.shahid.ecommerce.dto.cart.CartItemResponse;
import com.shahid.ecommerce.dto.cart.CartResponse;
import com.shahid.ecommerce.dto.category.CategoryResponse;
import com.shahid.ecommerce.dto.order.OrderItemResponse;
import com.shahid.ecommerce.dto.order.OrderResponse;
import com.shahid.ecommerce.dto.product.ProductResponse;
import com.shahid.ecommerce.dto.user.UserResponse;
import com.shahid.ecommerce.model.AppUser;
import com.shahid.ecommerce.model.CartItem;
import com.shahid.ecommerce.model.Category;
import com.shahid.ecommerce.model.CustomerOrder;
import com.shahid.ecommerce.model.OrderItem;
import com.shahid.ecommerce.model.Product;

import java.math.BigDecimal;
import java.util.List;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static UserResponse toUser(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static CategoryResponse toCategory(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    public static ProductResponse toProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.isActive(),
                toCategory(product.getCategory()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public static CartResponse toCart(List<CartItem> cartItems) {
        List<CartItemResponse> items = cartItems.stream().map(DtoMapper::toCartItem).toList();
        int totalItems = items.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(items, totalItems, totalAmount);
    }

    public static OrderResponse toOrder(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems().stream().map(DtoMapper::toOrderItem).toList();
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                items,
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private static CartItemResponse toCartItem(CartItem item) {
        Product product = item.getProduct();
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                item.getQuantity(),
                subtotal
        );
    }

    private static OrderItemResponse toOrderItem(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
