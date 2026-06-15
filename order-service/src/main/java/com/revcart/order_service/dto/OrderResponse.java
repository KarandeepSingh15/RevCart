package com.revcart.order_service.dto;

import com.revcart.order_service.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        List<OrderItemResponse> items
) {
}