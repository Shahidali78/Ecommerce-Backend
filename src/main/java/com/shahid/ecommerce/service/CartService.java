package com.shahid.ecommerce.service;

import com.shahid.ecommerce.dto.cart.AddCartItemRequest;
import com.shahid.ecommerce.dto.cart.CartResponse;
import com.shahid.ecommerce.dto.cart.UpdateCartItemRequest;

public interface CartService {

    CartResponse getCart();

    CartResponse addItem(AddCartItemRequest request);

    CartResponse updateItem(Long itemId, UpdateCartItemRequest request);

    void removeItem(Long itemId);
}
