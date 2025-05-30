# Database Connection Guide

## Issue: Connection Refused Error

If you encounter the following error when starting the application:

```
Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
```

This means the application is trying to connect to a PostgreSQL database that is not running.

## Solution

The application is configured to use a PostgreSQL database that runs in a Docker container. To ensure the database is running before the application starts, you need to run the application with the `local` profile:

```bash
./gradlew bootRun -Plocal
```

This command will:
1. Start the required Docker containers (PostgreSQL and Zipkin)
2. Wait for the database to be ready
3. Start the application with the `local` Spring profile

## How It Works

The `build.gradle.kts` file contains custom tasks that manage Docker containers:

- `dockerComposeUp`: Starts the Docker containers and waits for the database to be ready
- `dockerComposeDown`: Stops the Docker containers

When you run the application with the `-Plocal` flag, the `bootRun` task depends on `dockerComposeUp`, ensuring that the Docker containers are started before the application.

## Stopping the Application and Containers

To stop the Docker containers after stopping the application, run:

```bash
./gradlew dockerComposeDown
```

## Manual Database Connection

If you need to connect to the database manually, use the following connection details:

- Host: localhost
- Port: 5432
- Database: vocabulary_db
- Username: vocabulary_user
- Password: vocabulary_password