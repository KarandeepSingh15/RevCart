package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.revcart.common_events.payload.OrderItemPayload;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@JsonTypeName("PaymentSucceededEvent")
public record PaymentSucceededEvent(
        UUID sagaId,
        Long orderId,
        List<OrderItemPayload> items,
        Instant timestamp
) implements SagaMessage {
}