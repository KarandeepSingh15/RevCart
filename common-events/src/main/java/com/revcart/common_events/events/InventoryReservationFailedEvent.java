package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;
import java.util.UUID;
@JsonTypeName("InventoryReservationFailedEvent")
public record InventoryReservationFailedEvent(
        UUID sagaId,
        Long orderId,
        String reason,
        Instant timestamp
) implements SagaMessage {
}