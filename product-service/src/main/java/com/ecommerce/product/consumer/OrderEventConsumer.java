package com.ecommerce.product.consumer;

import com.ecommerce.common.event.OrderPlacedEvent;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "order-placed", groupId = "product-group")
    public void consumeOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order: {}", event.getOrderId());
        
        try {
            for (OrderPlacedEvent.OrderItem item : event.getItems()) {
                productService.deductInventory(item.getProductId(), item.getQuantity());
            }
            log.info("Successfully updated inventory for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to update inventory for order: {}", event.getOrderId(), e);
            // In a real scenario, you might emit an "InventoryDeductionFailed" event
            // to trigger a saga compensation (e.g. cancel order)
        }
    }
}