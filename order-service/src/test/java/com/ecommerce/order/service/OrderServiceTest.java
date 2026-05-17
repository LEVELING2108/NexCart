package com.ecommerce.order.service;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;
    private UUID userId;
    private Order order;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderRequest = OrderRequest.builder()
                .items(List.of(OrderRequest.OrderItemRequest.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .price(BigDecimal.valueOf(100.0))
                        .build()))
                .build();

        order = Order.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .totalAmount(BigDecimal.valueOf(200.0))
                .items(List.of())
                .build();
    }

    @Test
    void placeOrder_ShouldReturnSuccess() throws JsonProcessingException {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        ApiResponse<OrderResponse> response = orderService.placeOrder(orderRequest, userId);

        assertTrue(response.isSuccess());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(outboxRepository, times(1)).save(any(OutboxEvent.class));
    }
}