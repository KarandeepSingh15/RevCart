package com.revcart.common_events.repository;

import com.revcart.common_events.entity.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, Long> {
    boolean existsBySagaIdAndMessageType(UUID sagaId, String messageType);
}
