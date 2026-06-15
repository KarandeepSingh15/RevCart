package com.revcart.order_service.service;

import com.revcart.order_service.dto.CreateOrderRequest;
import com.revcart.order_service.dto.OrderResponse;

import java.util.List;

public interface IOrderService {

    OrderResponse createOrder(Long customerId, String role, CreateOrderRequest request);
    OrderResponse getOrder(Long orderId, Long customerId);
    List<OrderResponse> getMyOrders(Long customerId);
}