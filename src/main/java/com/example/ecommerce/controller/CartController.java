package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * Manages the user’s shopping cart. All endpoints require the user to be
 * authenticated. The currently logged in user is determined from the
 * security context.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartController(CartService cartService,
                          UserRepository userRepository,
                          ProductRepository productRepository,
                          CartItemRepository cartItemRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @GetMapping
    public ResponseEntity<Cart> getMyCart() {
        User user = getCurrentUser();
        Cart cart = cartService.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(java.math.BigDecimal.ZERO);
            return cartService.save(newCart);
        });
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addItemToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity) {
        User user = getCurrentUser();
        Cart cart = cartService.findByUser(user).orElseThrow(() -> new IllegalStateException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        cartService.addItem(cart, product, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId) {
        User user = getCurrentUser();
        Cart cart = cartService.findByUser(user).orElseThrow(() -> new IllegalStateException("Cart not found"));
        CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            return ResponseEntity.status(403).body("Item does not belong to current user's cart");
        }
        cartService.removeItem(cart, item);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        User user = getCurrentUser();
        Cart cart = cartService.findByUser(user).orElseThrow(() -> new IllegalStateException("Cart not found"));
        cartService.clearCart(cart);
        return ResponseEntity.ok(cart);
    }
}