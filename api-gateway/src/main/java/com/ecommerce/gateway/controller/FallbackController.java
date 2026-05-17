package com.ecommerce.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user")
    public Mono<String> userServiceFallback() {
        return Mono.just("User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/product")
    public Mono<String> productServiceFallback() {
        return Mono.just("Product Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/order")
    public Mono<String> orderServiceFallback() {
        return Mono.just("Order Service is currently unavailable. Please try again later.");
    }
}