package com.revcart.common_events.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_messages", uniqueConstraints = @UniqueConstraint(columnNames = {"saga_id", "message_type"}))
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ProcessedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID sagaId;
    private String messageType;
    private LocalDateTime processedAt;
}
