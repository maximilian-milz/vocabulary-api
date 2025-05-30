# Vocabulary API

A RESTful API for vocabulary management built with Kotlin and Spring Boot.

## Project Description

This project provides an API for managing vocabulary items.

## Algorithm

The Vocabulary API uses the SuperMemo-2 spaced repetition algorithm to optimize vocabulary learning and retention. This algorithm works as follows:

### SuperMemo-2 Algorithm

The SuperMemo-2 algorithm calculates optimal intervals between vocabulary reviews based on the quality of recall:

1. **Quality Rating**: Each review is rated on a scale from 0–5:
   - 0: Complete blackout, no recognition
   - 1: Incorrect response, but recognized the word
   - 2: Incorrect response, but upon seeing the correct answer, it felt familiar
   - 3: Correct response, but required significant effort to recall
   - 4: Correct response, after some hesitation
   - 5: Correct response, perfect recall

2. **Ease Factor**: Each vocabulary item has an ease factor (default: 2.5) that adjusts based on recall quality:
   - The ease factor increases for good recalls and decreases for poor recalls
   - The minimum ease factor is 1.3 to prevent intervals from becoming too short

3. **Interval Calculation**:
   - First successful review (rating ≥ 3): Next review in 1 day
   - Second successful review: Next review in 6 days
   - Further reviews: Next review in (the previous interval × ease factor) days
   - Failed reviews (rating < 3): Reset repetition count and review again in 1 day

This algorithm ensures that well-remembered items are reviewed less frequently, while challenging items are reviewed more often, optimizing the learning process.

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
