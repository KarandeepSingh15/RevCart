package com.revcart.payment_service.consumer;

import com.revcart.common_events.constants.KafkaTopics;
import com.revcart.common_events.events.InventoryReservedEvent;
import com.revcart.common_events.service.MessageDeduplicationService;
import com.revcart.common_events.util.MessageMapper;
import com.revcart.payment_service.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public final class PaymentCommandConsumer {

    private final IPaymentService paymentService;
    private final MessageMapper messageMapper;
    private final MessageDeduplicationService messageDeduplicationService;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0))
    @KafkaListener(topics = KafkaTopics.PAYMENT_COMMAND_TOPIC, groupId = "payment-service")
    public void consumeInventoryReservedEvent(InventoryReservedEvent payload) {

//        InventoryReservedEvent event = messageMapper.read(payload, InventoryReservedEvent.class);
        String messageType = InventoryReservedEvent.class.getSimpleName();
        log.info("Received InventoryReservedEvent for order {} with sagaId {}", payload.orderId(), payload.sagaId());
        if (messageDeduplicationService.alreadyProcessed(payload.sagaId(), messageType)) {
            log.info("Duplicate message ignored for event {}:{}", payload.sagaId(), messageType);
            return;
        }
        paymentService.processPayment(payload,messageType);
    }

    @DltHandler
    public void handleDlt(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage) {

        log.error("Message sent to DLT from topic {} payload {}", topic, payload);

    }
}
