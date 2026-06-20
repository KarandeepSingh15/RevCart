package com.revcart.order_service.service;

import com.revcart.common_events.events.InventoryCommittedEvent;
import com.revcart.common_events.events.InventoryReleasedEvent;
import com.revcart.order_service.dto.CreateOrderRequest;
import com.revcart.order_service.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponse createOrder(Long customerId, String role, CreateOrderRequest request);
    OrderResponse getOrder(Long orderId, Long customerId);
    List<OrderResponse> getMyOrders(Long customerId);
    void confirmOrder(InventoryCommittedEvent event, String messageType);
    void cancelOrder(Long orderId, String reason, UUID sagaId, String messageType);
}