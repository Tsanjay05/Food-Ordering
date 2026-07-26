# Online Food Order Processing System

This production-ready system is structured as a Maven Multi-Module project with Java 21 backend microservices and a Vite-React-TypeScript frontend dashboard, orchestrated asynchronously by an embedded Camunda BPMN engine and ActiveMQ Classic.

---

## Technical Stack
- **Java 21**
- **Spring Boot 3.5.0** (Spring Boot starter libraries)
- **Maven Multi Module**
- **ActiveMQ Classic** (JMS message broker)
- **Camunda 7 Embedded** (Workflow engine orchestrator)
- **React + Vite + TypeScript** (Frontend SPA dashboard)
- **MySQL** (Data storage)
- **Flyway** (Database schema migrations)
- **MapStruct & Lombok** (Data mapping & model boilerplate reduction)
- **Docker Compose** (Container orchestration)

---

## Project Module Hierarchy
- `shared-library`: Common JMS events (`OrderCreatedEvent`) and shared utility resources.
- `order-service`: Gateway entry port (`8081`). Exposes REST API, embeds Camunda 7 engine, and publishes created orders to ActiveMQ.
- `payment-service`: Payment processor port (`8082`). Checks transaction thresholds and returns outcomes to Camunda service delegate.
- `kitchen-service`: Kitchen operations port (`8083`). Configures ticket creation records and logs readiness states.
- `delivery-service`: Delivery logic port (`8084`). Handles courier scheduling and routes logs.
- `frontend`: Operations dashboard UI port (`5173`).

---

## End-to-End Orchestration Sequence
1. **Submit Order**: Client browser posts order data to `POST http://localhost:8081/api/orders`.
2. **Publish Queue Event**: `order-service` writes the order to database in state `PLACED` and fires `OrderCreatedEvent` to ActiveMQ queue `order.created` returning `201 Created` immediately.
3. **Consume & Initiate**: `order-service`'s `OrderEventConsumer` listens on ActiveMQ, picks up the payload, and starts the Camunda Process Instance using `order-process` key.
4. **Step 1: Payment Check**: Camunda triggers `CallPaymentDelegate` which performs a REST POST to `payment-service` at `http://localhost:8082/api/payments`. 
   - *Failure Path*: If the payment fails (e.g., if the orderId is divisible by 5), Camunda gateways direct workflow to cancellation tasks, updating DB status to `CANCELLED`.
5. **Step 2: Kitchen Ticket**: If payment is successful, Camunda calls `CallKitchenDelegate` which calls `kitchen-service` REST API (`http://localhost:8083/api/kitchen`) to create and update kitchen status to `READY`.
6. **Step 3: Courier Assignment**: Camunda calls `CallDeliveryDelegate` which calls `delivery-service` REST API (`http://localhost:8084/api/delivery`) to schedule driver allocation logs.
7. **Complete Process**: Camunda workflow calls `CompleteWorkflowDelegate` updating local Order status to `DELIVERED` and logging execution completions.
8. **UI Live Tracker**: React frontend polls `GET http://localhost:8081/api/orders` every 2 seconds, displaying state progress. Selecting a row shows detailed step paths and operational trace logs.

---

## Setup & Execution

### Prerequisites
- Java 21 JDK
- Apache Maven (configured in environmental path variables)
- Docker Desktop with docker compose V2

### 1. Build the Artifacts
From the root repository folder, build and package all services:
```bash
mvn clean install
```
This runs validation, builds target packages for Java modules, and packages the `shared-library` JAR.

### 2. Boot up Services with Docker Compose
To compile docker containers and start the environment:
```bash
docker compose up -d --build
```
This builds image containers from local Dockerfiles, mounts databases, spins up ActiveMQ, starts Spring microservices, and loads Vite React dashboards.

### 3. Verify Applications
- **Frontend Dashboard**: Access `http://localhost:5173` to place orders and track transitions.
- **Camunda Web Console**: Access `http://localhost:8081/camunda` (Credentials: `admin` / `adminpassword`) to inspect detailed process traces.
- **ActiveMQ Broker Panel**: Access `http://localhost:8161` (Credentials: `admin` / `admin`) to monitor message broker states.
- **Swagger Documentation**: Access `http://localhost:8081/swagger-ui/index.html` to explore OpenAPI schemas.
