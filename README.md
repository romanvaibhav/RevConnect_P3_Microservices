# RevConnect-P3

## End-to-End Modernization of RevConnect Monolithic Social Media Platform into Microservices Architecture

---

## Project Overview

**RevConnect-P3** is a professional social networking platform inspired by **LinkedIn** that allows users to build professional connections, share posts, interact with content, and manage their network.

The project was originally built as a **monolithic full-stack application** and later modernized into a **cloud-native microservices architecture** using **Spring Boot** and **Spring Cloud**.

The application supports:

- Personal Users
- Content Creators
- Business Accounts

Users can create profiles, publish posts, interact with content, follow other users, and receive notifications for social activities.

---

## Key Features

### User Management

- Secure User Registration
- JWT Based Authentication
- Role Based Access Control (RBAC)
- Profile Management
- Privacy Settings

### Social Networking

- Create / Edit / Delete Posts
- Like / Unlike Posts
- Comment on Posts
- Share / Repost Posts
- Follow / Unfollow Users
- Send Connection Requests

### Content Discovery

- Personalized Feed
- Trending Posts
- Hashtag Search
- User Search

### Notifications

- Real-time engagement alerts
- Follow notifications
- Comment notifications
- Like notifications
- Connection request alerts

---

## Microservices Architecture

The monolithic backend was refactored into **domain-driven microservices**, where each service manages a specific business capability.

Each microservice has:

- Independent lifecycle
- Separate business logic
- Dedicated database schema
- REST-based communication

---

## Microservices in the System

| Service | Description |
|---------|-------------|
| `user-service` | User authentication, profile management, RBAC |
| `post-service` | Post creation, editing, deletion |
| `social-service` | Social interactions such as follows and connections |
| `notification-service` | Real-time notifications |
| `analytics-service` | Engagement analytics and metrics |
| `product-service` | Business products and promotions |
| `api-gateway` | Entry point for routing all requests |
| `eureka-server` | Service discovery |
| `config-server` | Centralized configuration management |

---

## System Architecture

The system uses:

- Angular Frontend
- Spring Cloud API Gateway
- Domain Driven Microservices
- Eureka Service Discovery
- Config Server
- MySQL Databases

---

## Technology Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Spring Cloud
- OpenFeign
- Maven

### Frontend

- Angular
- TypeScript
- Angular Router
- Angular Forms
- HTML5
- CSS3

### Database

- MySQL

### DevOps

- Docker
- Git
- Jenkins / GitHub Actions

---


## Project Structure

The project follows a multi-module microservices structure.

```bash
revconnect-microservices
├── analytics-service
├── api-gateway
├── config-server
├── eureka-server
├── notification-service
├── post-service
├── product-service
├── social-service
├── user-service
├── create-databases.sql
├── mvnw
├── mvnw.cmd
└── pom.xml
```

![Image](https://github.com/user-attachments/assets/f1a985d9-5cc8-40d9-b57c-82b78a4cb161)
![Image](https://github.com/user-attachments/assets/beee43b9-2819-4c10-8412-c42ed1f0b22e)
![Image](https://github.com/user-attachments/assets/aa7fa17b-185c-4266-adc8-75ffeb3376dc)
![Image](https://github.com/user-attachments/assets/d5b417db-fb69-4e45-bedf-2758d701eabf)
![Image](https://github.com/user-attachments/assets/50635e9e-725b-4966-9acd-96bd5cee6b92)
![Image](https://github.com/user-attachments/assets/76f52f6a-3ce1-4868-8868-cffff821e695)
![Image](https://github.com/user-attachments/assets/ffc76d1d-f85f-4e5c-a8dd-e63f7fc1a7dc)
![Image](https://github.com/user-attachments/assets/b68f27ab-386e-4049-9401-ba3ea2f02bec)
![Image](https://github.com/user-attachments/assets/5b81e845-c91f-4713-ab1c-4c183423054e)
![Image](https://github.com/user-attachments/assets/d1cc5719-0099-4391-9fb7-5839d250cf2c)
![Image](https://github.com/user-attachments/assets/04ae7c58-028e-43e8-b8de-1dea0c478714)
