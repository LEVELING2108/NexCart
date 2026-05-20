package com.ecommerce.order.consumer;

import com.ecommerce.common.event.PaymentProcessedEvent;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    @Test
    void consumePaymentProcessed_Success_ShouldUpdateOrderStatusToPaid() {
        UUID orderId = UUID.randomUUID();
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .orderId(orderId)
                .status("SUCCESS")
                .build();
        Order order = Order.builder().id(orderId).status(OrderStatus.PAYMENT_INITIATED).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        paymentEventConsumer.consumePaymentProcessed(event);

        verify(orderRepository).save(argThat(savedOrder -> 
            savedOrder.getStatus() == OrderStatus.PAID
        ));
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void consumePaymentProcessed_Failed_ShouldUpdateOrderStatusToFailedAndEmitCancelEvent() throws Exception {
        UUID orderId = UUID.randomUUID();
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .orderId(orderId)
                .status("FAILED")
                .build();
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PAYMENT_INITIATED)
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        paymentEventConsumer.consumePaymentProcessed(event);

        verify(orderRepository).save(argThat(savedOrder -> 
            savedOrder.getStatus() == OrderStatus.FAILED
        ));
        verify(outboxRepository).save(any(OutboxEvent.class));
    }
}
