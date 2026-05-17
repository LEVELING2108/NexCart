package com.ecommerce.payment.consumer;

import com.ecommerce.common.event.OrderPlacedEvent;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-placed", groupId = "payment-group")
    public void consumeOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order: {}", event.getOrderId());
        paymentService.processPayment(event);
    }
}