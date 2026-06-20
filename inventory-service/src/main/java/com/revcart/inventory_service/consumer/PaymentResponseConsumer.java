package com.revcart.inventory_service.consumer;

import com.revcart.common_events.constants.KafkaTopics;
import com.revcart.common_events.events.PaymentFailedEvent;
import com.revcart.common_events.events.PaymentSucceededEvent;
import com.revcart.common_events.events.ReserveInventoryCommand;
import com.revcart.common_events.events.SagaMessage;
import com.revcart.common_events.service.MessageDeduplicationService;
import com.revcart.inventory_service.exception.InsufficientStockAvailabilityException;
import com.revcart.inventory_service.exception.InventoryRecordNotFoundException;
import com.revcart.inventory_service.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0), exclude = {InventoryRecordNotFoundException.class, InsufficientStockAvailabilityException.class})
@KafkaListener(topics = KafkaTopics.PAYMENT_RESPONSE_TOPIC, groupId = "inventory-service")
public class PaymentResponseConsumer {

    private final IInventoryService inventoryService;
    private final MessageDeduplicationService messageDeduplicationService;

    @KafkaHandler
    public void handle(PaymentSucceededEvent event) {
        String messageType = PaymentSucceededEvent.class.getSimpleName();
        log.info("Received PaymentSucceededEvent for order {} with sagaId {}", event.orderId(), event.sagaId());
        if (checkDuplicate(event, messageType)) return;
        inventoryService.commitInventory(event, messageType);
    }

    @KafkaHandler
    public void handle(PaymentFailedEvent event) {
        String messageType = PaymentFailedEvent.class.getSimpleName();
        log.info("Received PaymentFailedEvent for order {} with sagaId {}", event.orderId(), event.sagaId());
        if (checkDuplicate(event, messageType)) return;
        inventoryService.releaseInventory(event, messageType);
    }

    private boolean checkDuplicate(SagaMessage event, String messageType) {
        if (messageDeduplicationService.alreadyProcessed(event.sagaId(), messageType)) {
            log.info("Duplicate message ignored for event {}:{}", event.sagaId(), messageType);
            return true;
        }
        return false;
    }

    @DltHandler
    public void handleDlt(SagaMessage payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage) {

        log.error("Message sent to DLT from topic {} payload {}", topic, payload);

    }

}
