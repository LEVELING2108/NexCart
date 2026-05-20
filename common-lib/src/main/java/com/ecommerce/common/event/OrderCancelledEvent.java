package com.ecommerce.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelledEvent {
    private UUID orderId;
    private List<OrderItem> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem {
        private UUID productId;
        private Integer quantity;
    }
}
