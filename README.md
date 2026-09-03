# Product Management API

A secure RESTful Product Management API built using **Java 17, Spring Boot, Spring Data JPA, PostgreSQL, Spring Security, JWT, and Docker**.

The application provides product CRUD operations, item management, JWT-based authentication with refresh tokens, role-based authorization, pagination, validation, standardized error handling, Swagger/OpenAPI documentation, and Docker Compose support.

---

## 🚀 Tech Stack

* **Java 17**
* **Spring Boot 4.1.1**
* **Spring Web**
* **Spring Data JPA / Hibernate**
* **PostgreSQL 16**
* **Spring Security**
* **JWT Authentication**
* **Refresh Tokens**
* **Jakarta Bean Validation**
* **Lombok**
* **JUnit 5**
* **Mockito**
* **Swagger / OpenAPI**
* **Maven**
* **Docker**
* **Docker Compose**

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/com/zestindia/productapi/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   │
│   └── resources/
│       └── application.yml
│
└── test/
    └── java/
```

### Architecture

```text
Client
   │
   ▼
REST Controller
   │
   ▼
Security / JWT Filter
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
PostgreSQL Database
```

The application follows a layered architecture:

* **Controller** — Handles HTTP requests and responses.
* **Service** — Contains business logic.
* **Repository** — Handles database operations using Spring Data JPA.
* **Entity** — Represents database tables.
* **DTO** — Controls request and response data.
* **Security** — Handles JWT authentication and authorization.
* **Exception** — Provides standardized API error responses.

---

## 🔐 Authentication

The API uses **JWT-based authentication** with access tokens and refresh tokens.

### Authentication Flow

```text
Register
   ↓
Login
   ↓
Access Token + Refresh Token
   ↓
Send Access Token with API requests
   ↓
Access Token expires
   ↓
Refresh Token
   ↓
New Access Token + Rotated Refresh Token
```

Protected endpoints require:

```text
Authorization: Bearer <access-token>
```

### Roles

The application supports:

* `ROLE_USER`
* `ROLE_ADMIN`

Product deletion requires the `ADMIN` role.

---

## 🌐 API Endpoints

Base URL:

```text
http://localhost:8080
```

### Authentication

| Method | Endpoint                | Description                   |
| ------ | ----------------------- | ----------------------------- |
| POST   | `/api/v1/auth/register` | Register a new user           |
| POST   | `/api/v1/auth/login`    | Login and receive JWT tokens  |
| POST   | `/api/v1/auth/refresh`  | Refresh and rotate JWT tokens |

### Products

| Method | Endpoint                      | Description             |
| ------ | ----------------------------- | ----------------------- |
| GET    | `/api/v1/products`            | Get paginated products  |
| GET    | `/api/v1/products/{id}`       | Get product by ID       |
| GET    | `/api/v1/products/{id}/items` | Get items for a product |
| POST   | `/api/v1/products`            | Create a product        |
| PUT    | `/api/v1/products/{id}`       | Update a product        |
| DELETE | `/api/v1/products/{id}`       | Delete a product        |

---

## 📄 Pagination

The product listing API supports pagination.

Example:

```text
GET /api/v1/products?page=0&size=10
```

---

## ✅ Validation

Product requests use Jakarta Bean Validation.

Example:

```json
{
  "productName": "Laptop"
}
```

Invalid requests are rejected with an appropriate API error response.

---

## 🗄️ Database

The application uses PostgreSQL.

### Database Configuration

Default local configuration:

```text
Database: productdb
Username: postgres
Password: postgres
Port: 5432
```

The application can also receive database configuration through environment variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```

---

## 🐳 Running with Docker

Make sure Docker Desktop is installed and running.

From the project root:

```powershell
docker compose up --build
```

The application will be available at:

```text
http://localhost:8081
```

PostgreSQL will run on:

```text
localhost:5432
```

To stop the application:

```powershell
docker compose down
```

To stop the application and remove the database volume:

```powershell
docker compose down -v
```

### Docker Architecture

```text
             Docker Compose
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
   Spring Boot API       PostgreSQL
   port 8081             port 5432
        │
        └────── JDBC ────────►
```

---

## 📖 Swagger / OpenAPI

Swagger provides interactive API documentation.

### Local

```text
http://localhost:8080/swagger-ui/index.html
```

### Docker

```text
http://localhost:8081/swagger-ui/index.html
```

Open Swagger and use the **Authorize** button to provide:

```text
Bearer <access-token>
```

---

## 🛠️ Running Locally

### 1. Clone the repository

```powershell
git clone https://github.com/PRohitD/product-management-api.git
cd product-management-api
```

### 2. Start PostgreSQL

Make sure PostgreSQL is running locally and the `productdb` database is available.

### 3. Build the application

Windows:

```powershell
.\mvnw.cmd clean package
```

### 4. Run the application

```powershell
.\mvnw.cmd spring-boot:run
```

The API will start on:

```text
http://localhost:8080
```

---

## 🧪 Testing

The project uses:

* JUnit 5
* Mockito
* Spring Boot Test
* H2

Run tests with:

```powershell
.\mvnw.cmd test
```

---

## 🔒 Security Features

* JWT access-token authentication
* Refresh-token authentication
* Refresh-token rotation
* Role-based authorization
* Password-based authentication
* Stateless Spring Security configuration
* Request validation
* CORS configuration
* Protected product endpoints

---

## 🗃️ Database Model

### Product

```text
Product
--------
id
product_name
created_by
created_on
modified_by
modified_on
```

### Item

```text
Item
----
id
quantity
product_id
```

Relationship:

```text
Product 1 ─────────── * Item
```

A product can contain multiple items.

---

## 📌 API Versioning

All REST APIs use versioned URLs:

```text
/api/v1/
```

This allows future API versions to be introduced without breaking existing clients.

---

## 📦 Docker Files

The project contains:

```text
Dockerfile
docker-compose.yml
```

The Docker Compose configuration starts:

1. Spring Boot application
2. PostgreSQL database

The application waits for PostgreSQL to become healthy before starting.

---

## 👨‍💻 Author

**Rohit Pawar**

GitHub:

https://github.com/PRohitD
