# NexCart - Project Instructions & Context

This file serves as the foundational memory for NexCart. It summarizes the architecture, implemented features, and the current development state to ensure continuity across sessions.

## 🏗️ Architecture Overview
NexCart is a microservices ecosystem built with **Spring Boot 3.2** and **Spring Cloud (2023.0.0)**.
- **Infrastructure:**
  - **Service Registry (Eureka):** Port 8761
  - **Config Server:** Port 8888 (Native profile)
  - **API Gateway:** Port 8080 (Circuit Breakers + JWT Filter)
- **Services:**
  - **User Service:** Auth, Registration, JWT.
  - **Product Service:** Catalog, Elasticsearch Search, Inventory Management.
  - **Order Service:** Order placement, Transactional Outbox.
  - **Payment Service:** Async payment processing.

## 🚀 Implemented Features
- **Reliable Messaging:** Transactional Outbox Pattern in Order Service.
- **Distributed Search:** Elasticsearch integration in Product Service with advanced filters (Price, Category) and Sorting (Price, Name).
- **Inventory Sync:** Auto-deduction of stock via Kafka events.
- **Observability:**
  - **Prometheus/Grafana:** Metrics and dashboards.
  - **Zipkin:** Distributed tracing.
  - **ELK Stack:** Centralized logging.
- **Resilience:** Resilience4j Circuit Breakers in API Gateway with fallbacks.
- **Documentation:** Swagger/OpenAPI integrated into all functional services.
- **Automation:** GitHub Actions CI pipeline and full Docker Compose orchestration.

## 📍 Current Status
- **Phase 4 & 5 Complete:** The system is enterprise-grade with full observability and automation.
- **Next Steps:**
  1. Implement **Saga Pattern (Choreography)** for distributed transaction management.
  2. Setup **Kubernetes** manifests for cluster deployment.
  3. Integrate **Keycloak** for professional IAM.
  4. Build a **React/Next.js Storefront**.

## 🛠️ Maintenance Notes
- **Secrets:** Always use placeholders (`REPLACE_WITH_YOUR_JWT_SECRET`) in config files before pushing to GitHub.
- **Docker:** Use `docker/docker-compose-full.yml` to spin up the entire stack.
