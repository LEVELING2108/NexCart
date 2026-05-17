package com.ecommerce.user.service;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.event.UserRegisteredEvent;
import com.ecommerce.user.entity.Role;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public ApiResponse<?> register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ApiResponse.builder().success(false).message("Email already exists").build();
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    public ApiResponse<?> login(String email, String password, AuthenticationManager authenticationManager) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        UserDetails userDetails = loadUserByUsername(email);
        String token = jwtService.generateToken(userDetails);
        
        return ApiResponse.builder()
                .success(true)
                .message("Login successful")
                .data(Map.of("token", token))
                .build();
    }
}
