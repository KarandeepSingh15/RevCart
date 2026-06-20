package com.revcart.order_service.consumer;

import com.revcart.common_events.constants.KafkaTopics;
import com.revcart.common_events.events.*;
import com.revcart.common_events.service.MessageDeduplicationService;
import com.revcart.order_service.exception.OrderNotFoundException;
import com.revcart.order_service.service.IOrderService;
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
@RequiredArgsConstructor
@Slf4j
@RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0), exclude = {OrderNotFoundException.class})
@KafkaListener(topics = KafkaTopics.INVENTORY_RESPONSE_TOPIC, groupId = "order-service")
public class InventoryResponseConsumer {

    private final IOrderService orderService;
    private final MessageDeduplicationService messageDeduplicationService;

    @KafkaHandler
    public void handle(InventoryReservationFailedEvent event) {
        String messageType = InventoryReservationFailedEvent.class.getSimpleName();
        log.info("Received InventoryReservationFailedEvent for order {} with sagaId {}", event.orderId(), event.sagaId());
        if (checkDuplicate(event, messageType)) return;
        orderService.cancelOrder(event.orderId(), event.reason(), event.sagaId(), messageType);
    }

    @KafkaHandler
    public void handle(InventoryReleasedEvent event) {
        String messageType = InventoryReleasedEvent.class.getSimpleName();
        log.info("Received InventoryReleasedEvent for order {} with sagaId {}", event.orderId(), event.sagaId());
        if (checkDuplicate(event, messageType)) return;
        orderService.cancelOrder(event.orderId(), event.reason(), event.sagaId(), messageType);
    }

    @KafkaHandler
    public void handle(InventoryCommittedEvent event) {
        String messageType = InventoryCommittedEvent.class.getSimpleName();
        log.info("Received InventoryCommittedEvent for order {} with sagaId {}", event.orderId(), event.sagaId());
        if (checkDuplicate(event, messageType)) return;
        orderService.confirmOrder(event, messageType);
    }

    @DltHandler
    public void handleDlt(SagaMessage payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage) {

        log.error("Message sent to DLT from topic {} payload {}", topic, payload);
    }

    private boolean checkDuplicate(SagaMessage event, String messageType) {
        if (messageDeduplicationService.alreadyProcessed(event.sagaId(), messageType)) {
            log.info("Duplicate message ignored for event {}:{}", event.sagaId(), messageType);
            return true;
        }
        return false;
    }
}