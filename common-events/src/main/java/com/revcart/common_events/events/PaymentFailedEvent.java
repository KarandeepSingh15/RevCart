package com.revcart.common_events.events;

import com.revcart.common_events.interfaces.SagaMessage;
import com.revcart.common_events.payload.OrderItemPayload;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID sagaId,
        Long orderId,
        List<OrderItemPayload> items,
        String reason,
        Instant timestamp

) implements SagaMessage {
}
