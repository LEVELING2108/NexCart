package com.ecommerce.order.consumer;

import com.ecommerce.common.event.InventoryReservationFailedEvent;
import com.ecommerce.common.event.InventoryReservedEvent;
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
public class InventoryEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "inventory-reserved", groupId = "order-group")
    @Transactional
    public void consumeInventoryReserved(InventoryReservedEvent event) {
        log.info("Received InventoryReservedEvent for order: {}", event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.PAYMENT_INITIATED);
            orderRepository.save(order);
            log.info("Updated order {} status to PAYMENT_INITIATED", event.getOrderId());
        }, () -> log.error("Order not found for inventory reserved event: {}", event.getOrderId()));
    }

    @KafkaListener(topics = "inventory-failed", groupId = "order-group")
    @Transactional
    public void consumeInventoryReservationFailed(InventoryReservationFailedEvent event) {
        log.info("Received InventoryReservationFailedEvent for order: {}. Reason: {}", event.getOrderId(), event.getReason());

        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            log.info("Updated order {} status to FAILED due to inventory reservation failure", event.getOrderId());
        }, () -> log.error("Order not found for inventory failure event: {}", event.getOrderId()));
    }
}
