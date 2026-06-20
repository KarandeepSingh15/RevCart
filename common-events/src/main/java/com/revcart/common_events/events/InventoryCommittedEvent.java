package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;
import java.util.UUID;
@JsonTypeName("InventoryCommittedEvent")
public record InventoryCommittedEvent(
        UUID sagaId,
        Long orderId,
        Instant timestamp
) implements SagaMessage {
}
