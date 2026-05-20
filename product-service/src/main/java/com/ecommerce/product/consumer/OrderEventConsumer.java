package com.ecommerce.product.consumer;

import com.ecommerce.common.event.InventoryReservationFailedEvent;
import com.ecommerce.common.event.InventoryReservedEvent;
import com.ecommerce.common.event.OrderCancelledEvent;
import com.ecommerce.common.event.OrderPlacedEvent;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-placed", groupId = "product-group")
    public void consumeOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order: {}", event.getOrderId());
        
        try {
            for (OrderPlacedEvent.OrderItem item : event.getItems()) {
                productService.deductInventory(item.getProductId(), item.getQuantity());
            }
            log.info("Successfully deducted inventory for order: {}", event.getOrderId());

            // Emit InventoryReservedEvent
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

            kafkaTemplate.send("inventory-reserved", reservedEvent);
            log.info("Published InventoryReservedEvent for order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Failed to update inventory for order: {}", event.getOrderId(), e);
            
            // Emit InventoryReservationFailedEvent
            InventoryReservationFailedEvent failedEvent = InventoryReservationFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .reason(e.getMessage() != null ? e.getMessage() : "Insufficient stock or product not found")
                    .build();
            
            kafkaTemplate.send("inventory-failed", failedEvent);
            log.info("Published InventoryReservationFailedEvent for order: {}", event.getOrderId());
        }
    }

    @KafkaListener(topics = "order-cancelled", groupId = "product-group")
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
}