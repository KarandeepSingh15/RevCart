package com.revcart.inventory_service.scheduler;

import com.revcart.common_outbox.publisher.OutboxPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryOutboxScheduler {

    private final OutboxPublisher publisher;

    @Scheduled(fixedDelay = 15000)
    public void publish() {
        publisher.publishPendingEvents();
    }
}

