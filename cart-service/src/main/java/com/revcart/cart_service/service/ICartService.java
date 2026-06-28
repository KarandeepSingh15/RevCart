package com.revcart.cart_service.service;

import com.revcart.cart_service.dto.AddCartItemRequest;
import com.revcart.cart_service.dto.CartResponse;
import com.revcart.cart_service.dto.UpdateCartItemRequest;

public interface ICartService {

    CartResponse getCart(Long userId);

    CartResponse addItem(Long userId, AddCartItemRequest request);

    CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request);

    void removeItem(Long userId, Long itemId);

    void clearCart(Long userId);
}