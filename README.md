# StayConnected 🏠

> **A full-stack property booking platform that connects travelers with their perfect stays**

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Messaging-black?style=flat-square&logo=apachekafka)](https://kafka.apache.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-green?style=flat-square)](https://www.thymeleaf.org/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-Auth-darkgreen?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)

---

## 📖 Table of Contents

- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Related Microservices](#related-microservices)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [Running All Services Together](#running-all-services-together)
- [Contributing](#contributing)

---

## Overview

**StayConnected** is a property booking and management platform that enables travelers to discover, browse, and book accommodations with ease. The platform is built as a **Spring Boot monolith** that handles all core business logic — user management, property listings, reviews, and more — and delegates specific domain responsibilities to two independent **microservices** via Apache Kafka for asynchronous, event-driven communication (for Email Service) and REST API for synchronous communication (for Booking Service). 

The result is a scalable, maintainable, and production-ready system that demonstrates real-world enterprise patterns.

---

## System Architecture

StayConnected uses a **hybrid architecture** built around a central Spring Boot monolith that manages the main business domain — users, transactions, wallets, authentication, property listings, reviews, platform logic and more. To improve scalability and separation of concerns, the system integrates with two dedicated microservices:

- **Booking Service** → handles reservations, cancellations, and availability checks through **REST API** (synchronous communication).
- **Email Service** → processes booking confirmations, alerts, and notifications through **Apache Kafka** (asynchronous communication).

This approach combines the simplicity of a monolith with the flexibility of microservices, ensuring better maintainability, fault isolation, and future scalability.


```
┌──────────────────────────────────────────────────────────────────────┐
│                    StayConnected Monolith (Spring Boot)              │
│                                                                      │
│  ┌───────────────┐   ┌───────────────┐   ┌─────────────────────────┐ │
│  │ User & Auth   │   │ Properties    │   │ Reviews / Favorites /   │ │
│  │ Management    │   │ Management    │   │ Platform Features /     │ │
│  │               │   │               │   │      And More           │ │   
│  └───────────────┘   └───────────────┘   └─────────────────────────┘ │
│                                                                      │
│   Spring Security • Thymeleaf • Spring Data JPA • Spring Validation  │
│   • Spring Boot • Spring Cache • Spring Web • MySQL • Testing        │
│   • Spring Cloud (OpenFeign) • H2 (in-memory DB) • Apache Kafka      │
│   • OAuth2 • Lombok • More...                                        │
└───────────────────────────────┬──────────────────────────────────────┘
                                │
                ┌───────────────┴──────────────────────┐
                │                                      │
        REST API (Sync)                        Apache Kafka (Async)
                │                                      │
        ┌───────▼───────────────┐              ┌───────▼───────────────┐
        │ Booking Service       │              │  Email Service        │                          
        │                       │              │                       │
        │ - Create booking      │              │ - Booking             │
        │ - Cancel booking      │              │   confirmations       │
        │ - Check dates         │              │ - Notifications       │
        │ - Availability        │              │                       │
        │ - User's reservations │              │ - Inquiry Emails      │
        │                       │              │   to Property Owner   │                
        │                       │              │ - Delivery logs       │
        └───────┬───────────────┘              └────────┬──────────────┘
                │                                       │
           MySQL Database                          MySQL Database
```

---

## Related Microservices

The StayConnected ecosystem consists of three repositories that work together:

| Service | Repository | Description |
|---|---|---|
| **Monolith (Core)** | [stay-connected](https://github.com/JekoGyulev/stay-connected) | Main application — users, transactions, properties, authentication & authorization, UI, and more... |
| **Booking Service** | [stay-connected-booking-service](https://github.com/JekoGyulev/stay-connected-booking-service) | Handles reservation creation, cancellation, and date availability checks |
| **Email Service** | [stay-connected-email-svc](https://github.com/JekoGyulev/stay-connected-email-svc) | Sends different types of email notifications triggered by Kafka events |

> **Note:** All three services must be running together for the full platform to function correctly. See [Running All Services Together](#running-all-services-together) for details.

---

## Features

### 👤 User Management & Authentication
- **User registration and login** with form-based authentication via Spring Security
- **OAuth2 Social Login** — sign in with Google or other providers, no password required
- **Secure password hashing** using Spring Security Crypto (BCrypt)
- **Role-based access control** — different permissions for guests, and hosts
- **Password change** with email notification sent automatically via the Email Service
- **Full control of account details** - able to modify username, profile photo, email and additional information connected to the account of the current user.
- **Wallets** - each user has a wallet, created and assigned after the login operation. The user can then create transactions (topping, book property and more) and the amount of money of a user's wallet stays up-to-date, allowing users to reliably track their own wallet that contains transactions history. 

### 🏡 Property Management
- **List a property** — hosts can create and publish property listings with descriptions, pricing, and availability, photos, location and many more...
- **Browse and search** — travelers can discover available properties at real-time.
- **Property detail pages** — full information, photos, host contact, reviews for property and many more...

### 📅 Booking & Reservations
- **Book a stay** — travelers select dates through interactive booking calendar and confirm reservations; delegated to the Booking Microservice
- **Cancel a booking** — guests can cancel bookings, triggering automatic email notifications
- **Availability checking** — real-time date availability validation via the Booking Microservice
- **OpenFeign integration** — the monolith communicates with the Booking Service over HTTP using a declarative Feign client

### 📬 Email Notifications (Event-Driven)
All email notifications are handled asynchronously by the Email Microservice, which subscribes to Kafka topics published by the monolith:

### Additional Stuff
Application supports multiple languages according to user's needs. Currently supported languages are : english, german, bulgarian and spanish. It is planned to make other languages also supported. 

Guests can contact owners of a property they would like to stay at in case they have any question. This is done by sending email inquiry to the property owner, the email contains everything essential (title, description, method of notifiying the host and so on...)





| Event | Email Sent |
|---|---|
| User registers | Welcome email |
| Booking confirmed | Booking confirmation |
| Booking cancelled | Cancellation & refund notice |
| Password changed | Security alert notification |
| Guest inquiry sent | Forwarded to property host |

### ⚡ Performance & Reliability
- **Spring Cache** — frequently accessed data is cached at the application level to reduce database load
- **Spring Boot Actuator** — production-ready health checks and application metrics endpoints
- **Spring Boot DevTools** — fast development feedback loop with live reload
- **H2 in-memory database** — used for integration testing, keeping tests fast and isolated

---

## Technology Stack

### Core Monolith

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.0 |
| UI / Templating | Thymeleaf + Thymeleaf Spring Security Extras |
| Security | Spring Security 6, Spring Security Crypto, OAuth2 Client |
| Database | MySQL, H2 |
| ORM | Spring Data JPA (Hibernate) |
| Messaging | Apache Kafka (Spring Kafka) |
| HTTP Client | Spring Cloud OpenFeign 4.2.0 |
| Object Mapping | ModelMapper 3.1.1 |
| Email Sending | Spring Boot Starter Mail |
| Validation | Spring Boot Starter Validation |
| Caching | Spring Boot Starter Cache |
| Monitoring | Spring Boot Actuator |
| Utilities | Lombok |
| Build Tool | Apache Maven (Maven Wrapper) |

### Booking Microservice

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.0 |
| Database | MySQL, H2 |
| ORM | Spring Data JPA |
| API Documentation | Springdoc OpenAPI / Swagger UI |
| Serialization | Jackson Datatype JSR310 (Java 8 Date/Time) |
| Utilities | Lombok |
| Build Tool | Apache Maven |

### Email Microservice

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.0 |
| Database | MySQL, H2 |
| ORM | Spring Data JPA |
| Messaging | Apache Kafka (Spring Kafka) |
| Email Sending | Spring Boot Starter Mail (Gmail SMTP) |
| API Documentation | Springdoc OpenAPI / Swagger UI |
| Utilities | Lombok |
| Build Tool | Apache Maven |

---

## Project Structure

```
stay-connected/                     ← This repository (monolith)
├── .mvn/wrapper    
├── src/
│   ├── main/
│   │   ├── java/com/example/stayconnected/
│   │   │   ├── aop /               ← AOP (Aspect-Oriented Programming)
│   │   │   ├── config/             ← Security, Kafka, BCryptPasswordEncoder, Locale 
│   │   │   ├── dashboard/
│   │   │   ├── email/
│   │   │   ├── event/
│   │   │   ├── handler/
│   │   │   ├── location/
│   │   │   ├── notification_preference/
│   │   │   ├── property/
│   │   │   ├── reservation/
│   │   │   ├── review/
│   │   │   ├── security/
│   │   │   ├── transaction/
│   │   │   ├── user/
│   │   │   ├── utils/
│   │   │   ├── wallet/
│   │   │   ├── web/
│   │   │   └── StayConnectedApplication.java
│   │   └── resources/
│   │       ├── static/          ← CSS, JS, images
│   │       ├── templates/             ← Thymeleaf HTML templates
│   │       └── application.properties
│   │       └── messages.properties
│   │       └── messages_bg.properties
│   │       └── messages_de.properties
│   │       └── messages_es.properties
│   │         
│   └── test/                       ← Unit and integration tests
├── .gitattributes
├── .gitignore
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

---

## Getting Started

### Prerequisites

Make sure you have the following installed before running the application:

- **Java 17+** — [Download](https://openjdk.org/projects/jdk/17/)
- **Apache Maven 3.8+** (or use the included `./mvnw` wrapper)
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/mysql/)
- **Apache Kafka** (with  KRaft) — [Quickstart Guide](https://kafka.apache.org/quickstart)
- **Git**

### Installation

1. **Clone the main repository:**

```bash
git clone https://github.com/JekoGyulev/stay-connected.git
cd stay-connected
```

2. **Clone the microservices** (required for full functionality):

```bash
# In a separate directory
git clone https://github.com/JekoGyulev/stay-connected-booking-service.git
git clone https://github.com/JekoGyulev/stay-connected-email-svc.git
```

### Configuration

#### 1. Create the MySQL Databases

Log into MySQL and run:

```sql
CREATE DATABASE stayconnected;
CREATE DATABASE booking_service;
CREATE DATABASE email_service;
```

#### 2. Configure the Monolith (`src/main/resources/application.properties`)

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/stayconnected?createDatabaseIfNotExist=true
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

# JPA
spring.jpa.hibernate.ddl-auto=update

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# JSON Serializer (for sending)
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# OAuth2 (Google)
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}

# OAuth2 (GitHub)
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
spring.security.oauth2.client.registration.github.scope=user:email

```

#### 3. Set Required Environment Variables

```bash
export GOOGLE_CLIENT_ID="your-google-oauth2-client-id"
export GOOGLE_CLIENT_SECRET="your-google-oauth2-client-secret"
export GITHUB_CLIENT_ID="your-github-client-id"
export GITHUB_CLIENT_SECRET="your-github-secret"
```

> **Tip:** For Gmail (when configuring the microservice `stay-connected-email-svc`, use an [App Password](https://support.google.com/accounts/answer/185833) rather than your account password. For Google OAuth2, create credentials in the [Google Cloud Console](https://console.cloud.google.com/).

### Running the Application

Start Kafka first with Docker as container, then launch each service:

```bash
# 1. Pull Kafka Image from Docker Registry (DockerHub)
docker pull apache/kafka:3.9.0 (with KRaft) 

# 2. Start a container from this pulled image
docker run -d --name kafka \
-p 9092:9092 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
apache/kafka:3.9.0
```

The main application will be available at: **http://localhost:8080**

---

## Running All Services Together

For the complete platform to work, all three services must be running simultaneously. Start them in this order:

| Step | Service | Default Port | Command |
|---|---|---|---|
| 1 | Apache Kafka | `9092` | (via Docker) |
| 2 | Booking Service | `8081` | `./mvnw spring-boot:run` (in booking-service dir) |
| 3 | Email Service | `8082` | `./mvnw spring-boot:run` (in email-svc dir) |
| 4 | StayConnected (Monolith) | `8080` | `./mvnw spring-boot:run` (in stay-connected dir) |

### API Documentation

Once the services are running, you can explore their REST APIs via Swagger UI:

- **Email Service API:** http://localhost:8082/swagger-ui.html

### Health Checks

The monolith exposes Spring Boot Actuator endpoints for monitoring:

- **Health:** http://localhost:8080/actuator/health
- **Info:** http://localhost:8080/actuator/info

---

## Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add: your feature description'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

---

<p align="center">
  Built with ❤️ using Spring Boot
</p>
