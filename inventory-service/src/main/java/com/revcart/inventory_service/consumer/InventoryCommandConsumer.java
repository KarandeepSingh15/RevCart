package com.revcart.inventory_service.consumer;

import com.revcart.common_events.commands.ReserveInventoryCommand;
import com.revcart.common_events.constants.KafkaTopics;
import com.revcart.common_events.events.InventoryReservationFailedEvent;
import com.revcart.common_events.service.MessageDeduplicationService;
import com.revcart.common_events.util.MessageMapper;
import com.revcart.common_outbox.service.OutboxService;
import com.revcart.inventory_service.exception.InsufficientStockAvailabilityException;
import com.revcart.inventory_service.exception.InventoryRecordNotFoundException;
import com.revcart.inventory_service.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryCommandConsumer {

    private final IInventoryService inventoryService;
    private final MessageMapper messageMapper;
    private final MessageDeduplicationService messageDeduplicationService;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0), exclude = {InventoryRecordNotFoundException.class, InsufficientStockAvailabilityException.class})
    @KafkaListener(topics = KafkaTopics.INVENTORY_COMMAND_TOPIC, groupId = "inventory-service")
    public void consumeReserveInventoryCommand(String payload) {

        ReserveInventoryCommand command = messageMapper.read(payload, ReserveInventoryCommand.class);
        String messageType = ReserveInventoryCommand.class.getSimpleName();
        log.info("Received ReserveInventoryCommand for order {} with sagaId {}", command.orderId(), command.sagaId());
        if (messageDeduplicationService.alreadyProcessed(command.sagaId(), messageType)) {
            log.info("Duplicate message ignored for event {}:{}", command.sagaId(), messageType);
            return;
        }
        try {
            inventoryService.reserveInventory(command, messageType);
        } catch (InventoryRecordNotFoundException | InsufficientStockAvailabilityException e) {
            inventoryService.handleReservationFailed(command, e, messageType);
        }
    }


    @DltHandler
    public void handleDlt(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage) {

        log.error("Message sent to DLT from topic {} payload {}", topic, payload);

    }

}
