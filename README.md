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
