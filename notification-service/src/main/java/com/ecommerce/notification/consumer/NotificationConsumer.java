package com.ecommerce.notification.consumer;

import com.ecommerce.common.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    @KafkaListener(topics = "user-registered", groupId = "notification-group")
    public void consumeUserRegistered(UserRegisteredEvent event) {
        log.info("Sending welcome email to {} ({})", event.getFirstName(), event.getEmail());
        // Simulation of email sending logic
    }
}
