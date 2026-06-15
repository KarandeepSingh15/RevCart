package com.revcart.common_events.payload;

public record OrderItemPayload(
        Long productId,
        Integer quantity
) {
}
