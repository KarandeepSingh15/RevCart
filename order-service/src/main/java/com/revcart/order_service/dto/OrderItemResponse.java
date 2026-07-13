package com.revcart.order_service.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String productName
) {
}
