# Student Management REST API

This project is a Java 21 Spring Boot application that exposes CRUD endpoints for managing students using an H2 in-memory database.

## Features
- Student entity with fields: id, name, email, course
- Layered architecture: controller, service, repository
- H2 database configuration
- Validation support
- CRUD REST endpoints

## Run the application

```bash
mvn spring-boot:run
```

## Endpoints

- `GET /api/students`
- `GET /api/students/{id}`
- `POST /api/students`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`

## Example request body

```json
{
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "course": "Computer Science"
}
```

## H2 Console

Open: `http://localhost:8080/h2-console`

Use these settings:
- JDBC URL: `jdbc:h2:mem:studentdb`
- Username: `sa`
- Password: blank
