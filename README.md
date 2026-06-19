# ChatSphere X

A production-grade distributed real-time chat platform built with microservices architecture, supporting private messaging, group communication, presence tracking, media sharing, notifications, and audit logging.

## Overview

ChatSphere X is a scalable distributed chat system designed to simulate enterprise-grade communication architecture. The project demonstrates modern backend engineering principles including microservices, API Gateway routing, service discovery, asynchronous communication, distributed caching, WebSocket messaging, and centralized auditing.

The system is built to support:

* Secure JWT-based authentication
* Real-time one-to-one messaging
* Group chats and channel management
* User presence tracking
* Media uploads
* Notification handling
* Analytics and audit logs
* Distributed event-driven communication

---

## Architecture

### Core Infrastructure

* **Config Server** → Centralized configuration management
* **Discovery Server (Eureka)** → Service registration/discovery
* **API Gateway** → Unified entry point for all services
* **RabbitMQ** → Async event communication
* **Redis** → Presence tracking, caching, distributed locking
* **PostgreSQL** → Centralized multi-schema database

---

### Microservices

| Service              | Responsibility                                        |
| -------------------- | ----------------------------------------------------- |
| Auth Service         | Registration, login, JWT generation, token validation |
| User Service         | Profile management, user search                       |
| Chat Service         | Private messaging, WebSocket handling                 |
| Group Service        | Group creation, membership management                 |
| Presence Service     | Online/offline user tracking                          |
| Notification Service | Push/email notifications                              |
| Media Service        | File/image uploads                                    |
| Audit Service        | Security and activity logs                            |
| Analytics Service    | System metrics and usage analytics                    |

---

## Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Spring Cloud Config
* Spring Cloud Eureka
* Spring WebSocket
* Spring Data JPA
* Hibernate
* Flyway

### Database & Messaging

* PostgreSQL
* Redis
* RabbitMQ

### Frontend

* React
* Vite
* Tailwind CSS

### Deployment

* Docker
* Render

---

## Key Features

### Authentication

* User registration
* Login/logout
* JWT access token generation
* Protected APIs
* Role-based access control

### Chat

* Private one-to-one messaging
* Real-time WebSocket delivery
* Message persistence
* Read receipts

### Group Communication

* Group creation
* Member management
* Group broadcasting
* Role-based group controls

### Presence Tracking

* Online/offline status
* Active session monitoring

### Media Sharing

* Upload images/files
* Media attachment in chats

### Notifications

* Async push notifications
* Email event notifications

### Analytics & Auditing

* User activity logs
* Security audits
* Messaging analytics

---

## Distributed System Highlights

* Supports concurrent WebSocket connections
* Fault-tolerant message delivery
* Redis Pub/Sub synchronization
* RabbitMQ event-driven architecture
* JWT-based stateless authentication
* API Gateway routing and filtering
* Centralized auditing
* Multi-schema PostgreSQL design
* Horizontal service scalability

---

## Deployment Architecture

Frontend:
React SPA deployed on Render

Backend:
Multiple Spring Boot microservices deployed independently on Render

Database:
Managed PostgreSQL with isolated schemas:

* auth_schema
* user_schema
* chat_schema
* group_schema
* audit_schema
* analytics_schema

---

## Local Setup

### Clone repository

```bash
git clone <repository-url>
cd chatsphere-x
```

### Run infrastructure

```bash
docker-compose up -d
```

### Start services

Run in order:

```bash
config-server
discovery-server
api-gateway
auth-service
user-service
chat-service
group-service
presence-service
notification-service
media-service
audit-service
analytics-service
```

---

## Environment Variables

Example:

```env
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
SPRING_REDIS_HOST=
SPRING_RABBITMQ_HOST=
```

---

## API Flow

### Registration

```text
Frontend → API Gateway → Auth Service → PostgreSQL
```

### Login

```text
Frontend → API Gateway → Auth Service → JWT Response
```

### Private Chat

```text
Client A ↔ Gateway ↔ Chat Service ↔ Redis/PostgreSQL ↔ Client B
```

### Group Chat

```text
Client ↔ Gateway ↔ Group Service ↔ Chat Service
```

---

## Production Notes

For free-tier deployments:

* Static routing can be used in API Gateway to bypass Eureka.
* Config server can be bypassed using environment variables.
* Keep-alive pingers can reduce cold starts.

Recommended for production:

* Always-on instances
* Dedicated Redis
* Managed RabbitMQ
* Object storage for media
* Load balancing

---

## Future Improvements

* Message encryption
* Typing indicators
* Video/audio calls
* Push notifications
* AI moderation
* Chat backups
* Message reactions
* Search indexing

---

## Project Status

Current status: Active development

Core architecture: Complete
Deployment: Functional
Optimization: Ongoing

---

## Author

Built as a distributed systems engineering project demonstrating scalable backend design, real-time communication, and microservice orchestration.
