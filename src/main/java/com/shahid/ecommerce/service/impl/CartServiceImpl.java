package com.shahid.ecommerce.service.impl;

import com.shahid.ecommerce.dto.cart.AddCartItemRequest;
import com.shahid.ecommerce.dto.cart.CartResponse;
import com.shahid.ecommerce.dto.cart.UpdateCartItemRequest;
import com.shahid.ecommerce.exception.BadRequestException;
import com.shahid.ecommerce.exception.ResourceNotFoundException;
import com.shahid.ecommerce.model.AppUser;
import com.shahid.ecommerce.model.CartItem;
import com.shahid.ecommerce.model.Product;
import com.shahid.ecommerce.repository.CartItemRepository;
import com.shahid.ecommerce.repository.ProductRepository;
import com.shahid.ecommerce.service.CartService;
import com.shahid.ecommerce.service.CurrentUserService;
import com.shahid.ecommerce.service.DtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public CartServiceImpl(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            CurrentUserService currentUserService
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {
        AppUser user = currentUserService.requireCurrentUser();
        return loadCart(user.getId());
    }

    @Override
    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        AppUser user = currentUserService.requireCurrentUser();
        Product product = productRepository.findByIdForUpdate(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product not found");
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUser(user);
                    newItem.setProduct(product);
                    return newItem;
                });
        int requestedQuantity = item.getQuantity() + request.quantity();
        ensureStock(product, requestedQuantity);
        item.setQuantity(requestedQuantity);
        cartItemRepository.save(item);
        return loadCart(user.getId());
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long itemId, UpdateCartItemRequest request) {
        AppUser user = currentUserService.requireCurrentUser();
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product not found");
        }
        ensureStock(product, request.quantity());
        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
        return loadCart(user.getId());
    }

    @Override
    @Transactional
    public void removeItem(Long itemId) {
        AppUser user = currentUserService.requireCurrentUser();
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    private CartResponse loadCart(Long userId) {
        return DtoMapper.toCart(cartItemRepository.findAllByUserIdOrderByIdAsc(userId));
    }

    private void ensureStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new BadRequestException(
                    "Only " + product.getStock() + " unit(s) of " + product.getName() + " are available"
            );
        }
    }
}
