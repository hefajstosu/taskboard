# TaskBoard

Simple kanban board / task manager. REST API in Java 23 + Spring Boot 3.3, plus a small UI in plain JS.

## Features

- projects, members, roles (owner / member). non-members receive 404
- columns with optional WIP limit
- tasks - priority, assignee, due date, comments
- move tasks between columns (drag & drop in the UI)
- whole board in one request (no N+1)
- search tasks with filters + paging
- JWT auth
- errors as ProblemDetail
- swagger

## Stack

- Java 23, Spring Boot 3.3.4 (web, data jpa, security, validation)
- PostgreSQL 16 + Flyway
- JWT with spring oauth2 resource server
- springdoc-openapi
- JUnit 5, Mockito, Testcontainers (real Postgres)
- Docker, GitHub Actions

Also tried some newer java stuff - records everywhere, sealed interface + pattern matching in switch (WIP check), virtual threads.

## Running

Needs JDK 23 and Docker.

```
./mvnw spring-boot:run
```

Postgres is started automatically from docker-compose.yml (port 5433 because 5432 was taken on my machine).

App: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html

Everything in docker:

```
docker compose --profile app up --build
```

## API

Register and login:

```
curl -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"email":"ann@example.com","displayName":"Ann","password":"password123"}'

curl -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"ann@example.com","password":"password123"}'
```

Then send the token as `Authorization: Bearer ...`. Main endpoints:

- `/api/projects` - projects
- `/api/projects/{id}/members`
- `/api/projects/{id}/columns`
- `/api/projects/{id}/board` - columns with tasks
- `/api/projects/{id}/tasks?q=&priority=&assigneeId=&dueBefore=` - search
- `/api/columns/{id}/tasks`, `/api/tasks/{id}`
- `PATCH /api/tasks/{id}/move` - `{"columnId": 2, "position": 0}`
- `/api/tasks/{id}/comments`

Rest is in swagger.

## Tests

```
./mvnw verify
```

Integration test uses Testcontainers, its skipped if there is no docker.
