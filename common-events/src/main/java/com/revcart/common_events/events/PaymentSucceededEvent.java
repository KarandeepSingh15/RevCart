package com.revcart.common_events.events;

import com.revcart.common_events.interfaces.SagaMessage;
import com.revcart.common_events.payload.OrderItemPayload;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PaymentSucceededEvent(
        UUID sagaId,
        Long orderId,
        List<OrderItemPayload> items,
        Instant timestamp
) implements SagaMessage {
}