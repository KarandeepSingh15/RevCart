package com.revcart.order_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.revcart.order_service.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        List<OrderItemResponse> items,
        String cancellationReason
) {
}