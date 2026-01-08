# Librarby Project
Project repo for "Object-Oriented Technologies" course, Computer Science AGH 2025

> \* *Welcome to the library.* \
> \* *Yes, we know.* \
> \* *The sign is misspelled.*



**Librarby** is a RESTful backend application for managing a library system.  
It provides functionality for user management, book inventory, rentals, reservations, and authentication, with a strong focus on clean architecture and clear API contracts.

The project was developed as part of the **Object-Oriented Technologies** course, Computer Science AGH 2025.

---

## Features

- User management (Readers, Librarians, Admins)
- JWT-based authentication and role-based authorization
- Book, edition, and physical copy management
- Rentals and reservations
- Structured error handling
- Interactive API documentation with Swagger/OpenAPI

---

## Technologies Used

- **Java 25**
- **Spring Boot**
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **Hibernate / JPA**
- **MySQL**
- **Docker & Docker Compose** (local database)
- **Gradle**
- **Swagger / OpenAPI (springdoc-openapi)**

---

## How to Run the Project

### Prerequisites

- Java 25
- Docker & Docker Compose
- Git

The project includes the Gradle Wrapper (`gradlew`), so no local Gradle installation is required.

---

### Start the Database

A Docker Compose configuration is provided for local development.

```bash
cd docker
docker compose up -d
```

This starts a MySQL instance on port **3306**.

---

### Run the Backend

From the project root directory:

```bash
./gradlew bootRun
```

Alternatively, you can run the application directly from an IDE such as IntelliJ IDEA.

The backend will start on port **8080**.

---

## API Documentation (Swagger)

Once the application is running, the Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI specification (JSON) is available at:

```
http://localhost:8080/v3/api-docs
```

Most endpoints require authentication.  
To test secured endpoints in Swagger UI:
1. Log in using the authentication endpoint.
2. Copy the returned JWT token.
3. Authorize requests using the Bearer token mechanism in Swagger UI.

---

## Authentication Overview

- JWT-based authentication
- Stateless security model
- Role-based access control:
    - **Reader**
    - **Librarian**
    - **Admin**

Passwords are securely hashed using **BCrypt**.

---

## Project Status

The project is under active development and follows milestone-based progress.  
Planned features include:
- Book reviews and ratings
- Statistics and reporting
- Pagination, filtering, and search
- Improved authentication (refresh tokens, logout)
- Optional frontend application

---

## Academic Context

This project was created for a university course focused on object-oriented technologies, backend architecture, and secure API development.  
Design decisions prioritize clarity, maintainability, and extensibility.