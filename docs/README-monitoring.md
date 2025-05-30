# Monitoring and Observability Implementation

This document describes the monitoring and observability features that have been implemented in the Vocabulary API project.

## 1. Logging Framework Configuration

A comprehensive logging configuration has been set up in `application.yml`:

```yaml
logging:
  level:
    root: INFO
    me.maximilianmilz.vocabulary: DEBUG
    org.springframework.web: INFO
    org.hibernate: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 7
```

This configuration:
- Sets appropriate log levels for different packages
- Defines a consistent log format for both console and file output
- Configures log file rotation with size and history limits

## 2. Metrics with Micrometer

Micrometer has been configured to collect metrics from the application:

- Added Micrometer dependencies in `build.gradle.kts`:
  ```kotlin
  implementation("io.micrometer:micrometer-registry-prometheus")
  ```

- Configured Micrometer in `application.yml`:
  ```yaml
  management:
    metrics:
      distribution:
        percentiles-histogram:
          http.server.requests: true
  ```

- Created a `TracingAspect` class that uses Micrometer to record method execution times and error counts.

## 3. Health Checks and Actuator Endpoints

Spring Boot Actuator has been configured to provide health checks and monitoring endpoints:

- Added Actuator dependency in `build.gradle.kts`:
  ```kotlin
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  ```

- Configured Actuator endpoints in `application.yml`:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health,info,prometheus,metrics,loggers
      base-path: /actuator
    endpoint:
      health:
        show-details: always
      prometheus:
        enabled: true
  ```

## 4. Distributed Tracing

Distributed tracing has been implemented using Micrometer Tracing with Brave and Zipkin:

- Added tracing dependencies in `build.gradle.kts`:
  ```kotlin
  implementation("io.micrometer:micrometer-tracing-bridge-brave")
  implementation("io.zipkin.reporter2:zipkin-reporter-brave")
  ```

- Configured tracing in `application.yml`:
  ```yaml
  management:
    tracing:
      sampling:
        probability: 1.0
      enabled: true

  spring.zipkin:
    base-url: http://localhost:9411
    enabled: true
  ```

- Added Zipkin to `docker-compose.yml` to collect and visualize traces:
  ```yaml
  zipkin:
    image: openzipkin/zipkin:latest
    ports:
      - "9411:9411"
    networks:
      - app-network
    healthcheck:
      test: ["CMD", "wget", "-q", "--spider", "http://localhost:9411/health"]
      interval: 5s
      timeout: 5s
      retries: 5
  ```

## Testing the Monitoring Features

To test these monitoring and observability features:

1. Start the application with Docker Compose:
   ```
   ./gradlew bootRun -Plocal
   ```

   This command will start both the Docker containers and the application.

2. Access the following endpoints:
   - Actuator endpoints: http://localhost:8080/actuator
   - Health check: http://localhost:8080/actuator/health
   - Metrics: http://localhost:8080/actuator/metrics
   - Prometheus metrics: http://localhost:8080/actuator/prometheus
   - Zipkin UI: http://localhost:9411

3. Make some API requests to generate traces and metrics.

4. Check the logs in the `logs/application.log` file.

## Additional Notes

- The `TracingAspect` class provides method-level tracing for all public methods in the application, domain, and API packages.
- Metrics are automatically collected for HTTP requests, JVM statistics, and custom method execution times.
- Health checks include information about the application, disk space, and database connection.
- Distributed tracing allows you to visualize request flows through the application.
