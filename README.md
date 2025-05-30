# Vocabulary API

A RESTful API for vocabulary management built with Kotlin and Spring Boot.

## Project Description

This project provides an API for managing vocabulary items.

## Technologies

- Kotlin
- Spring Boot
- Java 21
- Gradle
- Spring Web
- Spring Validation
- Jackson
- SpringDoc OpenAPI
- JUnit 5
- PostgreSQL
- Flyway
- Docker

## Running the Application

To run the application with the required database:

```bash
./gradlew bootRun -Plocal
```

This command will start the required Docker containers (PostgreSQL and Zipkin) and then start the application.

## Documentation

- [Database Connection Guide](docs/README-database.md) - How to connect to the database and troubleshoot connection issues
- [Monitoring Guide](docs/README-monitoring.md) - Information about monitoring and observability features
