package com.ecommerce.payment.service;

import com.ecommerce.common.event.InventoryReservedEvent;
import com.ecommerce.common.event.PaymentProcessedEvent;
import com.ecommerce.payment.entity.OutboxEvent;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.OutboxRepository;
import com.ecommerce.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processPayment(InventoryReservedEvent event) {
        log.info("Processing payment for order: {}", event.getOrderId());

        // Simulation of payment processing (e.g. calling Razorpay/Stripe)
        boolean paymentSuccessful = true; // Simulating success

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .status(paymentSuccessful ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .transactionId(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Create PaymentProcessedEvent
        PaymentProcessedEvent processedEvent = PaymentProcessedEvent.builder()
                .orderId(savedPayment.getOrderId())
                .paymentId(savedPayment.getId())
                .status(savedPayment.getStatus().name())
                .message(paymentSuccessful ? "Payment successful" : "Payment failed")
                .build();

        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(savedPayment.getOrderId().toString())
                    .aggregateType("PAYMENT")
                    .eventType("PAYMENT_PROCESSED")
                    .destinationTopic("payment-processed")
                    .payload(objectMapper.writeValueAsString(processedEvent))
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();
            outboxRepository.save(outboxEvent);
            log.info("Saved PaymentProcessedEvent to outbox for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error serializing payment event", e);
            throw new RuntimeException("Failed to process payment due to event serialization error");
        }

        log.info("Payment processed for order: {}. Status: {}", event.getOrderId(), savedPayment.getStatus());
    }
}