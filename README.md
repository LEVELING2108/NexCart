# NexCart - E-Commerce Microservices Platform

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white)
![Tailwind](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

NexCart is a modern, scalable e-commerce platform built with a microservices architecture using **Java 17**, **Spring Boot 3.2**, **Spring Cloud**, and **Next.js**. It leverages event-driven communication (Saga Pattern), distributed searching (Elasticsearch), and centralized IAM (Keycloak) to provide a robust shopping experience.

## 🚀 Architecture Overview

### Storefront
- **Next.js Storefront**: Modern customer-facing web application built with React, TypeScript, Tailwind CSS, and Zustand.

### Core Services:
- **API Gateway**: Central entry point with OAuth2 Token Relay and Resilience4j circuit breakers.
- **Keycloak IAM**: Centralized Identity and Access Management for professional-grade security.
- **Config Server**: Centralized configuration management for all microservices.
- **Service Registry**: Eureka server for service discovery.
- **User Service**: Manages user profiles and registration synchronization.
- **Product Service**: Handles product catalog, inventory management, and high-performance search using **Elasticsearch**.
- **Order Service**: Manages the order lifecycle with **Saga Choreography** and **Transactional Outbox**.
- **Payment Service**: Asynchronous payment processing.

## 🛠 Tech Stack

- **Frontend**: Next.js 14, React 18, Tailwind CSS, Zustand, NextAuth.js
- **Backend**: Spring Boot 3.2.x, Spring Cloud 2023.0.0, Spring Security OAuth2
- **Databases**: PostgreSQL, Elasticsearch, Redis
- **Messaging**: Apache Kafka
- **Identity**: Keycloak
- **Observability**: Prometheus, Grafana, Zipkin, ELK Stack
- **Build Tools**: Maven, NPM
- **Containerization**: Docker, Kubernetes (Kustomize)

## ✨ Key Features

- **Distributed Transactions**: Implements the **Saga Pattern** for reliable cross-service operations.
- **Advanced Search**: High-performance search with category/price filters, powered by Elasticsearch.
- **Reliable Messaging**: **Transactional Outbox Pattern** across Order, Product, and Payment services.
- **Production Ready**: Kubernetes manifests with health probes, resource limits, and secrets management.

## 🚦 Getting Started

### Prerequisites:
- JDK 17
- Maven 3.8+
- Node.js 18+
- Docker & Docker Compose

### 1. Run the Backend:
```bash
docker-compose -f docker/docker-compose-full.yml up -d
mvn clean install -DskipTests
```

### 2. Run the Storefront:
```bash
cd storefront
npm install
npm run dev
```
Open `http://localhost:3000` to view the store.

## 🛡 Security Note

Keycloak admin credentials: `admin/admin`. Use the `nexcart` realm.
Kubernetes secrets are managed in `k8s/deployments/secrets.yaml`.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
