package com.ecommerce.product.config;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Seeding initial product data...");

            List<ProductRequest> initialProducts = List.of(
                ProductRequest.builder()
                    .name("Smartphone X1")
                    .description("The latest flagship smartphone with amazing camera and battery life.")
                    .price(new BigDecimal("999.99"))
                    .stockQuantity(50)
                    .categoryName("Electronics")
                    .build(),
                ProductRequest.builder()
                    .name("Wireless Noise-Cancelling Headphones")
                    .description("Premium sound quality with advanced noise cancellation technology.")
                    .price(new BigDecimal("299.99"))
                    .stockQuantity(100)
                    .categoryName("Electronics")
                    .build(),
                ProductRequest.builder()
                    .name("Professional DSL-R Camera")
                    .description("Capture every moment in stunning detail with this pro-grade camera.")
                    .price(new BigDecimal("1299.50"))
                    .stockQuantity(25)
                    .categoryName("Electronics")
                    .build(),
                ProductRequest.builder()
                    .name("Classic Cotton T-Shirt")
                    .description("Soft, breathable 100% cotton t-shirt for everyday comfort.")
                    .price(new BigDecimal("19.99"))
                    .stockQuantity(500)
                    .categoryName("Clothing")
                    .build(),
                ProductRequest.builder()
                    .name("Slim Fit Denim Jeans")
                    .description("Stylish and durable slim-fit jeans made from high-quality denim.")
                    .price(new BigDecimal("59.90"))
                    .stockQuantity(200)
                    .categoryName("Clothing")
                    .build(),
                ProductRequest.builder()
                    .name("Ergonomic Office Chair")
                    .description("Work comfortably with this adjustable ergonomic chair.")
                    .price(new BigDecimal("189.00"))
                    .stockQuantity(30)
                    .categoryName("Home")
                    .build(),
                ProductRequest.builder()
                    .name("Smart Coffee Maker")
                    .description("Brew the perfect cup of coffee from your smartphone.")
                    .price(new BigDecimal("89.95"))
                    .stockQuantity(75)
                    .categoryName("Home")
                    .build()
            );

            initialProducts.forEach(productService::createProduct);
            log.info("Data seeding completed.");
        }
    }
}
