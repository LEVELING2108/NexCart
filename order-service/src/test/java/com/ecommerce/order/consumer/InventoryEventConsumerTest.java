package com.ecommerce.order.consumer;

import com.ecommerce.common.event.InventoryReservationFailedEvent;
import com.ecommerce.common.event.InventoryReservedEvent;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEventConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private InventoryEventConsumer inventoryEventConsumer;

    @Test
    void consumeInventoryReserved_ShouldUpdateOrderStatusToPaymentInitiated() {
        UUID orderId = UUID.randomUUID();
        InventoryReservedEvent event = InventoryReservedEvent.builder()
                .orderId(orderId)
                .build();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        inventoryEventConsumer.consumeInventoryReserved(event);

        verify(orderRepository).save(argThat(savedOrder -> 
            savedOrder.getStatus() == OrderStatus.PAYMENT_INITIATED
        ));
    }

    @Test
    void consumeInventoryReservationFailed_ShouldUpdateOrderStatusToFailed() {
        UUID orderId = UUID.randomUUID();
        InventoryReservationFailedEvent event = InventoryReservationFailedEvent.builder()
                .orderId(orderId)
                .reason("Out of stock")
                .build();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        inventoryEventConsumer.consumeInventoryReservationFailed(event);

        verify(orderRepository).save(argThat(savedOrder -> 
            savedOrder.getStatus() == OrderStatus.FAILED
        ));
    }
}
