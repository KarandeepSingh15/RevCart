package com.revcart.common_events.events;

import com.revcart.common_events.interfaces.SagaMessage;
import com.revcart.common_events.payload.OrderItemPayload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID sagaId,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        List<OrderItemPayload> items,
        Instant timestamp
) implements SagaMessage {
}