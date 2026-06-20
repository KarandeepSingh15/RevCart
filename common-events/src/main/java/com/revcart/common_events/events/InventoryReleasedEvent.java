package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;
import java.util.UUID;
@JsonTypeName("InventoryReleasedEvent")
public record InventoryReleasedEvent(
        UUID sagaId,
        Long orderId,
        String reason,
        Instant timestamp
) implements SagaMessage {
}
