package com.revcart.common_outbox.publisher;

import com.revcart.common_outbox.entity.OutboxEvent;
import com.revcart.common_outbox.enums.OutboxStatus;
import com.revcart.common_outbox.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;


    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findPendingEvents();
        for (OutboxEvent event : pendingEvents) {
            publishEvent(event);
        }
    }

    @Transactional
    private void publishEvent(OutboxEvent event) {
        try {
            kafkaTemplate.send(event.getDestinationTopic(), event.getAggregateId(), event.getPayload()).get(10, TimeUnit.SECONDS);
            event.setStatus(OutboxStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());

            outboxRepository.save(event);

        } catch (Exception ex) {

            log.error("Failed to publish event {}", event.getId(), ex);
            event.setStatus(OutboxStatus.FAILED);
            outboxRepository.save(event);
        }
    }
}