package com.revcart.common_events.interfaces;

import java.time.Instant;
import java.util.UUID;

public interface SagaMessage {
    UUID sagaId();
    Long orderId();
    Instant timestamp();
}
