package com.ecommerce.user.service;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.event.UserRegisteredEvent;
import com.ecommerce.user.entity.Role;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ApiResponse<?> register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ApiResponse.builder().success(false).message("Email already exists").build();
        }
        
        user.setRole(user.getRole() != null ? user.getRole() : Role.BUYER);
        User savedUser = userRepository.save(user);

        // Publish event
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
        
        kafkaTemplate.send("user-registered", event);

        return ApiResponse.builder().success(true).message("User registered successfully").data(savedUser).build();
    }
}
