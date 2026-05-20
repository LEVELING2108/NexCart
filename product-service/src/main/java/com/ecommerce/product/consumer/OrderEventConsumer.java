package com.ecommerce.product.consumer;

import com.ecommerce.common.event.InventoryReservationFailedEvent;
import com.ecommerce.common.event.InventoryReservedEvent;
import com.ecommerce.common.event.OrderCancelledEvent;
import com.ecommerce.common.event.OrderPlacedEvent;
import com.ecommerce.product.entity.OutboxEvent;
import com.ecommerce.product.repository.OutboxRepository;
import com.ecommerce.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductService productService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-placed", groupId = "product-group")
    @Transactional
    public void consumeOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order: {}", event.getOrderId());

        try {
            for (OrderPlacedEvent.OrderItem item : event.getItems()) {
                productService.deductInventory(item.getProductId(), item.getQuantity());
            }
            log.info("Successfully deducted inventory for order: {}", event.getOrderId());

            // Create InventoryReservedEvent
            InventoryReservedEvent reservedEvent = InventoryReservedEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .totalAmount(event.getTotalAmount())
                    .items(event.getItems().stream()
                            .map(item -> InventoryReservedEvent.OrderItem.builder()
                                    .productId(item.getProductId())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            saveOutboxEvent(event.getOrderId().toString(), "PRODUCT", "INVENTORY_RESERVED", "inventory-reserved", reservedEvent);
            log.info("Saved InventoryReservedEvent to outbox for order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Failed to update inventory for order: {}", event.getOrderId(), e);

            // Create InventoryReservationFailedEvent
            InventoryReservationFailedEvent failedEvent = InventoryReservationFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .reason(e.getMessage() != null ? e.getMessage() : "Insufficient stock or product not found")
                    .build();

            saveOutboxEvent(event.getOrderId().toString(), "PRODUCT", "INVENTORY_FAILED", "inventory-failed", failedEvent);
            log.info("Saved InventoryReservationFailedEvent to outbox for order: {}", event.getOrderId());
        }
    }

    @KafkaListener(topics = "order-cancelled", groupId = "product-group")
    @Transactional
    public void consumeOrderCancelled(OrderCancelledEvent event) {
        log.info("Received OrderCancelledEvent for order: {}", event.getOrderId());
        try {
            for (OrderCancelledEvent.OrderItem item : event.getItems()) {
                productService.restoreInventory(item.getProductId(), item.getQuantity());
            }
            log.info("Successfully restored inventory for cancelled order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to restore inventory for cancelled order: {}", event.getOrderId(), e);
        }
    }

    private void saveOutboxEvent(String aggregateId, String aggregateType, String eventType, String destinationTopic, Object payload) {
        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(aggregateId)
                    .aggregateType(aggregateType)
                    .eventType(eventType)
                    .destinationTopic(destinationTopic)
                    .payload(objectMapper.writeValueAsString(payload))
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();
            outboxRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Error serializing outbox event", e);
            throw new RuntimeException("Failed to save outbox event due to serialization error");
        }
    }
}