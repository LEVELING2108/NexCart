package com.ecommerce.order.consumer;

import com.ecommerce.common.event.PaymentProcessedEvent;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-processed", groupId = "order-group")
    @Transactional
    public void consumePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent for order: {}. Status: {}", event.getOrderId(), event.getStatus());
        
        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            if ("SUCCESS".equals(event.getStatus())) {
                order.setStatus(OrderStatus.PAID);
            } else {
                order.setStatus(OrderStatus.FAILED);
            }
            orderRepository.save(order);
            log.info("Updated order {} status to {}", event.getOrderId(), order.getStatus());
        }, () -> log.error("Order not found: {}", event.getOrderId()));
    }
}