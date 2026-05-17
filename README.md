# NexCart - E-Commerce Microservices Platform

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

NexCart is a modern, scalable e-commerce backend built with a microservices architecture using **Java 17**, **Spring Boot 3.2**, and **Spring Cloud**. It leverages event-driven communication, distributed searching, and reliable transaction management to provide a robust shopping experience.

## 🚀 Architecture Overview

The system is composed of several specialized microservices communicating asynchronously via **Apache Kafka** and discovering each other through **Netflix Eureka**.

### Core Services:
- **API Gateway**: Central entry point using Spring Cloud Gateway with JWT-based authentication.
- **Config Server**: Centralized configuration management for all microservices.
- **Service Registry**: Eureka server for service discovery.
- **User Service**: Manages user registration, authentication, and JWT issuance.
- **Product Service**: Handles product catalog, inventory management, and high-performance search using **Elasticsearch**.
- **Order Service**: Manages the order lifecycle, utilizing the **Transactional Outbox Pattern** for reliable event publishing.
- **Payment Service**: Processes transactions and integrates with order status updates.
- **Notification Service**: Asynchronously sends alerts (simulated) based on system events.

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.x, Spring Cloud 2023.0.0
- **Languages**: Java 17
- **Databases**: PostgreSQL (Relational), Elasticsearch (Search), Redis (Caching)
- **Messaging**: Apache Kafka
- **Security**: Spring Security, JWT
- **Observability**: Zipkin (Distributed Tracing)
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose

## ✨ Key Features

- **Advanced Search**: Search products by name/description with filters for category and price range, powered by Elasticsearch.
- **Reliable Messaging**: Implements the **Transactional Outbox Pattern** in the Order Service to ensure events are never lost, even if the message broker is down.
- **Event-Driven Inventory**: Automated stock deduction in the Product Service triggered by new orders via Kafka.
- **Payment Integration**: Asynchronous payment processing that automatically updates order statuses to `PAID` or `FAILED`.
- **Centralized Config**: Manage all service properties in one place via the Config Server.

## 🚦 Getting Started

### Prerequisites:
- JDK 17
- Maven 3.8+
- Docker & Docker Compose

### Local Setup:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/LEVELING2108/NexCart.git
   cd NexCart
   ```

2. **Spin up Infrastructure**:
   Use Docker Compose to start PostgreSQL, Kafka, Elasticsearch, and Redis:
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

3. **Build the Project**:
   ```bash
   mvn clean install
   ```

4. **Run Services**:
   Start the services in the following order:
   - `service-registry`
   - `config-server`
   - `api-gateway`
   - All other functional services (`user-service`, `product-service`, etc.)

## 🛡 Security Note

For production or GitHub forks, ensure you update the following placeholders in your `config-server` YAML files:
- `REPLACE_WITH_YOUR_JWT_SECRET`
- Database credentials
- External API keys (e.g., Razorpay)

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
