package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.revcart.common_events.payload.OrderItemPayload;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@JsonTypeName("PaymentFailedEvent")
public record PaymentFailedEvent(
        UUID sagaId,
        Long orderId,
        List<OrderItemPayload> items,
        String reason,
        Instant timestamp

) implements SagaMessage {
}
