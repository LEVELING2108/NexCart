package com.ecommerce.payment.service;

import com.ecommerce.common.event.OrderPlacedEvent;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_ShouldSavePaymentAndEmitEvent() {
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .totalAmount(BigDecimal.valueOf(100.0))
                .build();

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(event.getOrderId())
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        paymentService.processPayment(event);

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(kafkaTemplate, times(1)).send(eq("payment-processed"), any());
    }
}