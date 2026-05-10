# Design Decisions And Assumptions

This document provides a concise explanation of the architectural choices and assumptions used in the Task Management API implementation.

## 1) Design Decisions

- **Layered architecture (`controller -> service -> repository`)**
  - Chosen to enforce separation of concerns and keep business logic out of transport and persistence layers.
  - Improves testability and maintainability.

- **In-memory persistence (`ConcurrentHashMap`)**
  - Explicitly selected to match the case-study requirement (no external DB).
  - `AtomicLong` is used for thread-safe, auto-generated IDs.

- **DTO pattern (`TaskRequestDto`, `TaskResponseDto`) + mapper**
  - API contracts are separated from domain model representation.
  - Prevents accidental overexposure of internal fields and supports future schema evolution.

- **Validation strategy**
  - Standard constraints: `@Valid`, `@NotBlank`, `@NotNull`, `@Size`.
  - Validation groups: `OnCreate`, `OnUpdate` to apply different rules per operation.
  - Custom validation: `@ValidDueDate` to reject past due dates when provided.

- **Centralized exception handling**
  - `@RestControllerAdvice` ensures consistent error responses.
  - Custom exceptions (`TaskNotFoundException`, `InvalidTaskException`) map to clear HTTP semantics.
  - Constraint and method validation errors are normalized into `400 Bad Request`.

- **Security and authentication**
  - Stateless JWT authentication was added as a bonus production feature.
  - `/auth/login` remains public; `/tasks/**` endpoints require Bearer token.
  - Demo in-memory user simplifies local testing and keeps scope focused.

- **Observability and docs**
  - SLF4J logging is used in service and exception paths.
  - OpenAPI/Swagger is enabled for discoverability and faster API verification.

- **Testing approach**
  - Unit tests (Mockito) cover service-level behavior.
  - Integration test (MockMvc) validates API behavior for invalid past due dates.

## 2) Assumptions

- **Data lifecycle**
  - Data is non-persistent and resets on application restart (acceptable due to in-memory requirement).

- **Create semantics**
  - `status` is optional on create; defaults to `PENDING` when omitted.

- **Update semantics**
  - `PUT /tasks/{id}` is treated as full update, so `status` is required.

- **Due date policy**
  - `dueDate` is optional; if provided, it cannot be in the past.

- **Completion semantics**
  - `PATCH /tasks/{id}/complete` only updates task status to `COMPLETED` (and `updatedAt`).

- **Security scope**
  - Single demo user authentication is sufficient for this case study.
  - Advanced user/role management and refresh tokens are out of current scope.

- **HTTP response behavior**
  - Core responses follow case-study expectations: `200`, `201`, `204`, `400`, `404`.
  - `401` is returned for unauthorized access to secured endpoints.

## 3) Future Production Extensions (Out of Scope)

- Replace in-memory repository with a persistent store (e.g., PostgreSQL + JPA).
- Add migration/versioning (Flyway/Liquibase).
- Add refresh tokens, user management, and role-based authorization.
- Add structured logging/correlation IDs and metrics/tracing.
- Add controller-level contract tests for all endpoints.

