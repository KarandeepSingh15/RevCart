package com.revcart.cart_service.dto;

import java.util.List;

public record CartResponse(
        Long cartId,
        Long userId,
        List<CartItemResponse> items
) {
}