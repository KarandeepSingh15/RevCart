package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.revcart.common_events.payload.OrderItemPayload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@JsonTypeName("InventoryReservedEvent")
public record InventoryReservedEvent(
        UUID sagaId,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        List<OrderItemPayload> items,
        Instant timestamp
) implements SagaMessage {
}