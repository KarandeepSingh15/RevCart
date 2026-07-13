package com.revcart.order_service.service.impl;

import com.revcart.common_events.constants.KafkaTopics;
import com.revcart.common_events.events.InventoryCommittedEvent;
import com.revcart.common_events.events.InventoryReleasedEvent;
import com.revcart.common_events.events.ReserveInventoryCommand;
import com.revcart.common_events.payload.OrderItemPayload;
import com.revcart.common_events.service.MessageDeduplicationService;
import com.revcart.common_outbox.service.OutboxService;
import com.revcart.order_service.dto.*;
import com.revcart.order_service.entity.Order;
import com.revcart.order_service.entity.OrderItem;
import com.revcart.order_service.enums.OrderStatus;
import com.revcart.order_service.exception.AccessDeniedException;
import com.revcart.order_service.exception.OrderNotFoundException;
import com.revcart.order_service.exception.ProductNotAvailableException;
import com.revcart.order_service.repository.OrderItemRepository;
import com.revcart.order_service.repository.OrderRepository;
import com.revcart.order_service.rest.ProductClient;
import com.revcart.order_service.service.IOrderService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements IOrderService {

    public static final String CUSTOMER = "CUSTOMER";
    public static final String ACTIVE = "ACTIVE";
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductFetchService  productFetchService;
    private final OutboxService outboxService;
    private final MessageDeduplicationService messageDeduplicationService;


    @Override
    @Transactional
    public OrderResponse createOrder(Long customerId, String role, CreateOrderRequest request) {
        validateCustomer(role);
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        UUID sagaId = UUID.randomUUID();
        Order order = Order.builder()
                .customerId(customerId)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.CREATED)
                .sagaId(sagaId)
                .build();
        Order savedOrder = orderRepository.save(order);

        for (OrderItemRequest item : request.items()) {
            try {
                ProductResponse product = productFetchService.getProduct(item.productId());
                if (!product.status().equals(ACTIVE)) {
                    throw new ProductNotAvailableException(String.valueOf(item.productId()));
                }
                BigDecimal itemTotal = product.price().multiply(BigDecimal.valueOf(item.quantity()));
                OrderItem orderItem = OrderItem.builder()
                        .orderId(savedOrder.getId())
                        .productId(item.productId())
                        .quantity(item.quantity())
                        .unitPrice(product.price())
                        .totalPrice(itemTotal)
                        .productName(product.name())
                        .build();
                orderItems.add(orderItem);
                totalAmount = totalAmount.add(itemTotal);
            } catch (FeignException e) {
                throw new ProductNotAvailableException(String.valueOf(item.productId()));
            }
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setTotalAmount(
                totalAmount);
        savedOrder = orderRepository.save(savedOrder);


        List<OrderItemPayload> payloadItems = orderItems.stream()
                .map(item -> new OrderItemPayload(item.getProductId(), item.getQuantity()))
                .toList();
        ReserveInventoryCommand command = new ReserveInventoryCommand(savedOrder.getSagaId(), savedOrder.getId(), customerId, totalAmount, payloadItems, Instant.now());
        outboxService.saveEvent("ORDER", savedOrder.getId().toString(), ReserveInventoryCommand.class.getSimpleName(), KafkaTopics.INVENTORY_COMMAND_TOPIC, command);
        return buildOrderResponse(savedOrder, orderItems);
    }

    private OrderResponse buildOrderResponse(Order savedOrder, List<OrderItem> orderItems) {
        List<OrderItemResponse> responses = orderItems.stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getProductName()))
                .toList();
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                responses,
                savedOrder.getStatus().equals(OrderStatus.CANCELLED)? savedOrder.getCancellationReason() : null
        );
    }

    @Override
    public OrderResponse getOrder(Long orderId, Long customerId) {
        Order order = getOrderEntity(orderId);
        if (!order.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("You can only view your orders");
        }
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return buildOrderResponse(order, items);

    }

    private Order getOrderEntity(Long orderId) {
        return orderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.valueOf(orderId)));
    }

    @Override
    public PageResponse<OrderResponse> getMyOrders(Long customerId, Pageable pageable) {
        return PageResponse.from(
                orderRepository.findByCustomerId(customerId, pageable),
                order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return buildOrderResponse(order, items);
                }
        );
    }

    @Override
    @Transactional
    public void confirmOrder(InventoryCommittedEvent event, String messageType) {
        Order order = getOrderEntity(event.orderId());
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        messageDeduplicationService.markProcessed(event.sagaId(), messageType);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId,String reason,UUID sagaId, String messageType) {
        Order order = getOrderEntity(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        orderRepository.save(order);
        messageDeduplicationService.markProcessed(sagaId, messageType);

    }

    private void validateCustomer(
            String role) {

        if (!CUSTOMER.equals(role)) {
            throw new AccessDeniedException("Only customers can place orders");
        }
    }
}