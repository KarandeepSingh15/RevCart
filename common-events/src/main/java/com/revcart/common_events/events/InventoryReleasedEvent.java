package com.revcart.common_events.events;

import com.revcart.common_events.interfaces.SagaMessage;
import java.time.Instant;
import java.util.UUID;

public record InventoryReleasedEvent(
        UUID sagaId,
        Long orderId,
        String reason,
        Instant timestamp
) implements SagaMessage {
}
