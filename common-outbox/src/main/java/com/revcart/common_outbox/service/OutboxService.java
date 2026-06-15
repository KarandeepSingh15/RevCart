package com.revcart.common_outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revcart.common_outbox.entity.OutboxEvent;
import com.revcart.common_outbox.enums.OutboxStatus;
import com.revcart.common_outbox.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public void saveEvent(String aggregateType, String aggregateId, String eventType, String topic, Object payload) {
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        OutboxEvent event = OutboxEvent.builder()
                            .aggregateType(
                                    aggregateType)
                            .aggregateId(
                                    aggregateId)
                            .eventType(
                                    eventType)
                            .destinationTopic(
                                    topic)
                            .payload(json)
                            .status(
                                    OutboxStatus.PENDING)
                            .createdAt(
                                    LocalDateTime.now())
                            .build();

        outboxRepository.save(event);
    }
}