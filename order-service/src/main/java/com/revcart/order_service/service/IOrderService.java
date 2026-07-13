package com.revcart.order_service.service;

import com.revcart.common_events.events.InventoryCommittedEvent;
import com.revcart.common_events.events.InventoryReleasedEvent;
import com.revcart.order_service.dto.CreateOrderRequest;
import com.revcart.order_service.dto.OrderResponse;
import com.revcart.order_service.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponse createOrder(Long customerId, String role, CreateOrderRequest request);
    OrderResponse getOrder(Long orderId, Long customerId);
    PageResponse<OrderResponse> getMyOrders(Long customerId, Pageable pageable);
    void confirmOrder(InventoryCommittedEvent event, String messageType);
    void cancelOrder(Long orderId, String reason, UUID sagaId, String messageType);
}