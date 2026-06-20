package com.revcart.payment_service.service.impl;

import com.revcart.common_events.constants.KafkaTopics;
import com.revcart.common_events.events.InventoryReservedEvent;
import com.revcart.common_events.events.PaymentFailedEvent;
import com.revcart.common_events.events.PaymentSucceededEvent;
import com.revcart.common_events.service.MessageDeduplicationService;
import com.revcart.common_outbox.service.OutboxService;
import com.revcart.payment_service.entity.Payment;
import com.revcart.payment_service.enums.PaymentStatus;
import com.revcart.payment_service.repository.PaymentRepository;
import com.revcart.payment_service.service.IPaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;
    private final MessageDeduplicationService messageDeduplicationService;

    @Override
    @Transactional
    public void processPayment(InventoryReservedEvent event, String messageType) {
        Payment payment = Payment.builder()
                .sagaId(event.sagaId())
                .orderId(event.orderId())
                .customerId(event.customerId())
                .amount(event.totalAmount())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        // Simulating gateway with 80%success and 20%failure
        boolean success = ThreadLocalRandom.current().nextInt(100) < 80;
        handleResult(payment, success, event, messageType);
    }

    private void handleResult(Payment payment, boolean success, InventoryReservedEvent event, String messageType) {
        if (success) {
            handleSuccessEvent(payment, event, messageType);
        } else {
            handleFailureEvent(payment, event, messageType);
        }
    }

    private void handleFailureEvent(Payment payment, InventoryReservedEvent event, String messageType) {
        PaymentFailedEvent failedEvent = new PaymentFailedEvent(event.sagaId(), event.orderId(), event.items(), "Payment declined", Instant.now());
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        outboxService.saveEvent("PAYMENT", event.orderId().toString(), PaymentFailedEvent.class.getSimpleName(), KafkaTopics.PAYMENT_RESPONSE_TOPIC, failedEvent);
        messageDeduplicationService.markProcessed(event.sagaId(), messageType);
    }

    private void handleSuccessEvent(Payment payment, InventoryReservedEvent event, String messageType) {
        PaymentSucceededEvent successEvent = new PaymentSucceededEvent(event.sagaId(), event.orderId(), event.items(), Instant.now());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        outboxService.saveEvent("PAYMENT", event.orderId().toString(), PaymentSucceededEvent.class.getSimpleName(), KafkaTopics.PAYMENT_RESPONSE_TOPIC, successEvent);
        messageDeduplicationService.markProcessed(event.sagaId(), messageType);
    }
}
