package com.revcart.common_events.service;

import com.revcart.common_events.entity.ProcessedMessage;
import com.revcart.common_events.repository.ProcessedMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageDeduplicationService {

    private final ProcessedMessageRepository repository;

    public boolean alreadyProcessed(UUID sagaId, String messageType) {
        return repository.existsBySagaIdAndMessageType(sagaId, messageType);
    }

    public void markProcessed(UUID sagaId, String messageType) {

        repository.save( ProcessedMessage.builder()
                        .sagaId(sagaId)
                        .messageType(messageType)
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }
}
