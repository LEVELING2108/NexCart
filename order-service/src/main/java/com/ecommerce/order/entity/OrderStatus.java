package com.ecommerce.order.entity;

public enum OrderStatus {
    PENDING,
    PAYMENT_INITIATED,
    PAID,
    FAILED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
