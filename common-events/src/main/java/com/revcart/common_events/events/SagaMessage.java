package com.revcart.common_events.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = ReserveInventoryCommand.class,
                name = "ReserveInventoryCommand"
        ),
        @JsonSubTypes.Type(
                value = InventoryReservedEvent.class,
                name = "InventoryReservedEvent"
        ),
        @JsonSubTypes.Type(
                value = InventoryReservationFailedEvent.class,
                name = "InventoryReservationFailedEvent"
        ),
        @JsonSubTypes.Type(
                value = PaymentSucceededEvent.class,
                name = "PaymentSucceededEvent"
        ),
        @JsonSubTypes.Type(
                value = PaymentFailedEvent.class,
                name = "PaymentFailedEvent"
        ),
        @JsonSubTypes.Type(
                value = InventoryCommittedEvent.class,
                name = "InventoryCommittedEvent"
        ),
        @JsonSubTypes.Type(
                value = InventoryReleasedEvent.class,
                name = "InventoryReleasedEvent"
        )
})
public sealed interface SagaMessage permits
        ReserveInventoryCommand,
        InventoryReservedEvent,
        InventoryReservationFailedEvent,
        PaymentSucceededEvent,
        PaymentFailedEvent,
        InventoryCommittedEvent,
        InventoryReleasedEvent {
    UUID sagaId();

    Long orderId();

    Instant timestamp();
}
