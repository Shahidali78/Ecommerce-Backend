# Ecommerce Backend

Spring Boot ecommerce backend with REST APIs for authentication, products, categories, carts, orders, users, and image metadata.

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- Spring Security
- JWT authentication
- MySQL
- Maven

## Project Structure

```text
src/main/java/com/example/ecommerce
├── config        # Spring Security configuration
├── controller    # REST API controllers
├── dto           # Request and response DTOs
├── model         # JPA entities
├── repository    # Spring Data repositories
├── security      # JWT and user details classes
└── service       # Business logic
```

## Requirements

- JDK 17 or newer
- Maven
- MySQL running locally

## Database Setup

Create a MySQL database:

```sql
CREATE DATABASE ecommerce;
```

Update `src/main/resources/application.properties` with your MySQL username and password:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

## Run The Application

From the project root:

```powershell
mvn spring-boot:run
```

The backend starts on:

```text
http://localhost:8080
```

## Main API Endpoints

Authentication:

```text
POST /api/auth/register
POST /api/auth/login
```

Products:

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

Categories:

```text
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

Carts:

```text
GET    /api/cart
POST   /api/cart/add/{productId}
DELETE /api/cart/item/{itemId}
DELETE /api/cart/clear
```

Orders:

```text
GET  /api/orders
GET  /api/orders/{id}
POST /api/orders
```

Users:

```text
GET    /api/users
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
```

## Authentication Flow

1. Register a user with `POST /api/auth/register`.
2. Login with `POST /api/auth/login`.
3. Use the returned JWT token in protected requests:

```text
Authorization: Bearer <token>
```

## Example Register Request

```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "test@example.com",
  "password": "password"
}
```

## Notes

- This repository contains the backend only. It does not include a frontend ecommerce website.
- `target/`, `.codex-run/`, and log files are local generated files and are not committed.
