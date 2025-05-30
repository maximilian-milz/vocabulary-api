plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "me.maximilian-milz"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.springdoc:springdoc-openapi-kotlin-dsl:2.5.0")

    // Monitoring and Observability
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Rate limiting dependencies
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // Caching
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")


    // Database dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql:42.7.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Testcontainers
    testImplementation("org.testcontainers:testcontainers:1.21.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Custom Docker tasks
tasks.register<Exec>("dockerComposeUp") {
    group = "docker"
    description = "Starts Docker containers using docker-compose"

    // Only execute when 'local' profile is active
    onlyIf {
        project.hasProperty("local")
    }

    commandLine("docker", "compose", "up", "-d")

    // Wait for the database to be ready
    doLast {
        println("Docker containers started. Waiting for database to be ready...")
        var ready = false
        var attempts = 0
        val maxAttempts = 10

        while (!ready && attempts < maxAttempts) {
            try {
                val process = ProcessBuilder("docker", "compose", "exec", "postgres", "pg_isready", "-U", "vocabulary_user", "-d", "vocabulary_db")
                    .redirectErrorStream(true)
                    .start()

                val exitCode = process.waitFor()
                ready = exitCode == 0

                if (!ready) {
                    println("Database not ready yet. Waiting...")
                    Thread.sleep(2000) // Wait 2 seconds before trying again
                    attempts++
                }
            } catch (e: Exception) {
                println("Error checking database readiness: ${e.message}")
                attempts++
                Thread.sleep(2000)
            }
        }

        if (ready) {
            println("Database is ready!")
        } else {
            println("Warning: Database might not be ready after $maxAttempts attempts")
        }
    }
}

tasks.register<Exec>("dockerComposeDown") {
    group = "docker"
    description = "Stops Docker containers using docker-compose"

    // Only execute when 'local' profile is active
    onlyIf {
        project.hasProperty("local")
    }

    commandLine("docker", "compose", "down")

    doLast {
        println("Docker containers stopped")
    }
}

// Make bootRun depend on dockerComposeUp to start Docker containers before the application
tasks.named("bootRun") {
    // Only depend on dockerComposeUp when 'local' profile is active
    if (project.hasProperty("local")) {
        dependsOn("dockerComposeUp")

        // Set Spring profile to 'local' when the 'local' Gradle property is set
        (this as JavaExec).systemProperty("spring.profiles.active", "local")

        doFirst {
            println("Starting application with Docker containers and 'local' Spring profile...")
        }

        doLast {
            println("Application started. To stop Docker containers when done, run './gradlew dockerComposeDown'")
        }
    } else {
        doFirst {
            println("Starting application without Docker containers (use -Plocal to start with Docker)...")
        }
    }
}

// Add a task to stop containers when the application stops
tasks.register("stopDockerContainers") {
    group = "application"
    description = "Stops Docker containers"

    // Only depend on dockerComposeDown when 'local' profile is active
    if (project.hasProperty("local")) {
        dependsOn("dockerComposeDown")
    } else {
        doLast {
            println("No Docker containers to stop (use -Plocal to enable Docker features)")
        }
    }
}
