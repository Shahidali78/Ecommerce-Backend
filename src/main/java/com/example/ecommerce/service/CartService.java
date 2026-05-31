package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Responsible for cart operations: adding items, removing items and clearing the cart.
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Optional<Cart> findByUser(User user) {
        return cartRepository.findByUser(user);
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    public void addItem(Cart cart, Product product, int quantity) {
        // In a real implementation you would check if the item exists and update quantity.
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        cart.getItems().add(item);
        recalculateTotal(cart);
        cartRepository.save(cart);
    }

    public void removeItem(Cart cart, CartItem item) {
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        recalculateTotal(cart);
        cartRepository.save(cart);
    }

    private void recalculateTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            total = total.add(item.getTotalPrice());
        }
        cart.setTotalPrice(total);
    }
}