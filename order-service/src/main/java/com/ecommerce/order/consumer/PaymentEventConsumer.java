package com.ecommerce.order.consumer;

import com.ecommerce.common.event.OrderCancelledEvent;
import com.ecommerce.common.event.PaymentProcessedEvent;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-processed", groupId = "order-group")
    @Transactional
    public void consumePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent for order: {}. Status: {}", event.getOrderId(), event.getStatus());
        
        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            if ("SUCCESS".equals(event.getStatus())) {
                order.setStatus(OrderStatus.PAID);
                orderRepository.save(order);
                log.info("Updated order {} status to PAID", event.getOrderId());
            } else {
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                log.info("Updated order {} status to FAILED. Emitting OrderCancelledEvent.", event.getOrderId());

                // Prepare OrderCancelledEvent
                List<OrderCancelledEvent.OrderItem> items = order.getItems().stream()
                        .map(item -> OrderCancelledEvent.OrderItem.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList());

                OrderCancelledEvent cancelledEvent = OrderCancelledEvent.builder()
                        .orderId(order.getId())
                        .items(items)
                        .build();

                try {
                    OutboxEvent outboxEvent = OutboxEvent.builder()
                            .aggregateId(order.getId().toString())
                            .aggregateType("ORDER")
                            .eventType("ORDER_CANCELLED")
                            .destinationTopic("order-cancelled")
                            .payload(objectMapper.writeValueAsString(cancelledEvent))
                            .createdAt(LocalDateTime.now())
                            .processed(false)
                            .build();
                    outboxRepository.save(outboxEvent);
                } catch (Exception e) {
                    log.error("Failed to serialize OrderCancelledEvent for order: {}", order.getId(), e);
                    throw new RuntimeException("Serialization failure for OrderCancelledEvent", e);
                }
            }
        }, () -> log.error("Order not found: {}", event.getOrderId()));
    }
}