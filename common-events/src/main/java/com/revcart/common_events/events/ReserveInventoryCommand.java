package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.revcart.common_events.payload.OrderItemPayload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@JsonTypeName("ReserveInventoryCommand")
public record ReserveInventoryCommand(
        UUID sagaId,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        List<OrderItemPayload> items,
        Instant timestamp
) implements SagaMessage {
}
