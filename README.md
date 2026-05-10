# Task Management API

Production-style **REST** task service built with **Java 17**, **Spring Boot 4.0.6**, and **layered architecture** (controller → service → repository), using an **in-memory** store, **DTOs**, **validation**, **global exception handling**, **JWT security**, **OpenAPI/Swagger**, **pagination and status filtering**, **Lombok**, **SLF4J logging**, **JUnit/Mockito** tests, and **Docker** support.

## Features

- CRUD tasks plus **PATCH** complete endpoint
- **ConcurrentHashMap** repository (thread-safe IDs via `AtomicLong`)
- **Bean Validation** (`@Valid`, `@NotBlank`, `@NotNull`, size limits, validation groups for create vs update, custom `@ValidDueDate`)
- **@ControllerAdvice** with `TaskNotFoundException`, `InvalidTaskException`, and validation error payloads
- **JWT** (`POST /auth/login`) protecting all `/tasks/**` routes
- **springdoc-openapi** — UI at `/swagger-ui.html`, spec at `/v3/api-docs`
- **GET /tasks** supports `page`, `size`, and optional `status` filter

## Design Decisions And Assumptions

> For submission as a separate deliverable, see **`DESIGN_DECISIONS.md`**.

### Design decisions

- **Layered architecture** (`controller → service → repository`) to keep concerns separated and make testing/refactoring easier.
- **In-memory repository (`ConcurrentHashMap`)** selected intentionally per case-study scope; `AtomicLong` provides deterministic, thread-safe id generation.
- **DTO + mapper pattern** used so API contracts are decoupled from internal domain model.
- **Validation strategy** uses both standard constraints and a custom `@ValidDueDate` rule (`dueDate` cannot be in the past) to demonstrate real-world input guardrails.
- **Error contract** is centralized in `GlobalExceptionHandler` to keep consistent JSON error responses across validation, business, and not-found scenarios.
- **JWT auth** protects all task endpoints while keeping `/auth/login` public to demonstrate production-style stateless security flow.

### Assumptions

- **Single-node runtime** is assumed; data is non-persistent and resets on restart (expected for in-memory implementation).
- **Task update semantics**: `PUT /tasks/{id}` is treated as full update where `status` is required.
- **Task creation semantics**: if `status` is omitted on create, it defaults to `PENDING`.
- **Due date rule**: `dueDate` is optional; if provided, it must be today or a future date.
- **Authentication model**: one demo in-memory user is used for simplicity; user management/roles are out of scope for this exercise.
- **Error codes** follow case-study expectations (`200/201/204/400/404`) with `401` for unauthorized access in secured routes.

## Prerequisites

- **JDK 17**
- **Maven 3.9+** (or use the included `mvnw` / `mvnw.cmd`)

## Run locally

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

Default server: **http://localhost:8080**

### Configuration (`src/main/resources/application.properties`)

| Property | Purpose |
|----------|---------|
| `app.security.jwt.secret` | HMAC signing secret (must be strong in production) |
| `app.security.jwt.expiration-ms` | JWT TTL (default 24h) |
| `app.security.demo-user.username` / `password` | Demo credentials for login |

## Docker

Build and run:

```bash
docker build -t task-management-api .
docker run --rm -p 8080:8080 task-management-api
```

Or **Docker Compose**:

```bash
docker compose up --build
```

## Testing

```bash
./mvnw test
```

Includes ** Mockito-based `TaskServiceTest`** (task creation defaulting to `PENDING`, completion flow, and error cases) plus a **Spring context smoke test**.

## API authentication

1. `POST /auth/login` with JSON body (see below).
2. Use response `accessToken` as: `Authorization: Bearer <token>` for all `/tasks` calls.

Unauthorized requests to protected routes receive **401** JSON from Spring Security.

## OpenAPI / Swagger

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

Use **Authorize** in Swagger UI and paste: `Bearer <your_jwt>`.

---

## Endpoints

All task routes require a **Bearer JWT** unless noted.

### POST `/auth/login` (public)

**Request**

```json
{
  "username": "demo",
  "password": "demo123"
}
```

**Response — 200 OK**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresInMs": 86400000
}
```

**Errors**

- **400** — invalid credentials or validation (empty username/password)

---

### GET `/tasks`

**Query parameters**

| Name | Required | Description |
|------|----------|-------------|
| `page` | no (default `0`) | Zero-based page index |
| `size` | no (default `20`, max `100`) | Page size |
| `status` | no | Filter: `PENDING`, `IN_PROGRESS`, or `COMPLETED` |

**Response — 200 OK**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Design API",
      "description": "OpenAPI + security",
      "dueDate": "2026-05-12",
      "status": "PENDING",
      "createdAt": "2026-05-10T10:15:30",
      "updatedAt": "2026-05-10T10:15:30"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

---

### GET `/tasks/{id}`

**Response — 200 OK**

```json
{
  "id": 1,
  "title": "Design API",
  "description": "OpenAPI + security",
  "dueDate": "2026-05-12",
  "status": "PENDING",
  "createdAt": "2026-05-10T10:15:30",
  "updatedAt": "2026-05-10T10:15:30"
}
```

**Errors**

- **404** — task id does not exist

---

### POST `/tasks`

**Request** (`status` optional; omitted → `PENDING`)

```json
{
  "title": "Implement repository",
  "description": "In-memory ConcurrentHashMap",
  "dueDate": "2026-05-20",
  "status": "IN_PROGRESS"
}
```

**Response — 201 CREATED** (also returns `Location: /tasks/{id}`)

```json
{
  "id": 2,
  "title": "Implement repository",
  "description": "In-memory ConcurrentHashMap",
  "dueDate": "2026-05-20",
  "status": "IN_PROGRESS",
  "createdAt": "2026-05-10T10:18:01",
  "updatedAt": "2026-05-10T10:18:01"
}
```

**Errors**

- **400** — validation (e.g. blank `title`)
  - also returned when `dueDate` is in the past

---

### PUT `/tasks/{id}`

**Request** (`status` required)

```json
{
  "title": "Implement repository",
  "description": "Add pagination query",
  "dueDate": "2026-05-22",
  "status": "IN_PROGRESS"
}
```

**Response — 200 OK**

```json
{
  "id": 2,
  "title": "Implement repository",
  "description": "Add pagination query",
  "dueDate": "2026-05-22",
  "status": "IN_PROGRESS",
  "createdAt": "2026-05-10T10:18:01",
  "updatedAt": "2026-05-10T10:20:44"
}
```

**Errors**

- **400** — validation
- **404** — task not found

---

### DELETE `/tasks/{id}`

**Response — 204 NO CONTENT** (empty body)

**Errors**

- **404** — task not found

---

### PATCH `/tasks/{id}/complete`

Sets `status` to **COMPLETED** and refreshes `updatedAt` only (no request body).

**Response — 200 OK**

```json
{
  "id": 2,
  "title": "Implement repository",
  "description": "Add pagination query",
  "dueDate": "2026-05-22",
  "status": "COMPLETED",
  "createdAt": "2026-05-10T10:18:01",
  "updatedAt": "2026-05-10T10:25:10"
}
```

**Errors**

- **400** — `InvalidTaskException` (e.g. already `COMPLETED`)
- **404** — task not found

---

## Error payload shape

```json
{
  "timestamp": "2026-05-10T10:30:00.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 42",
  "path": "/tasks/42"
}
```

Validation errors include `fieldErrors`:

```json
{
  "timestamp": "2026-05-10T10:30:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/tasks",
  "fieldErrors": {
    "title": "Title is required"
  }
}
```

---

## Postman

Import **`postman/TaskManagement.postman_collection.json`**.

1. Set collection variable **`baseUrl`** (default `http://localhost:8080`).
2. Run **Auth → Login (get JWT)** — the test script saves **`accessToken`**.
3. Run requests under **Tasks** (folder uses Bearer `{{accessToken}}`).
4. **Create task** saves the new id into **`taskId`** for follow-up calls.

---

## Project layout

```
src/main/java/com/example/TaskManagement/
├── TaskManagementApplication.java
├── config/           # Security, JWT properties, OpenAPI
├── controller/       # REST controllers
├── dto/              # Request/response DTOs
├── exception/        # Exceptions + @ControllerAdvice
├── mapper/           # Entity ↔ DTO mapping
├── model/            # Task entity + TaskStatus enum
├── repository/       # In-memory implementation
├── security/         # JWT service + filter
├── service/          # Interfaces
└── service/impl/     # Task business logic
```

---

## License

Demo / educational use. Replace secrets and demo users before any production deployment.
