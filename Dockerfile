# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy gradle files first for better layer caching
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradlew ./

# Download dependencies (will be cached if no changes)
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY src/ src/

# Build the application
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="EventHub Team <info@eventhub.com>"
LABEL version="1.0"
LABEL description="EventHub Event Service Microservice"

# Create a non-root user to run the application
RUN addgroup -S eventapp && adduser -S eventapp -G eventapp

# Set working directory
WORKDIR /app

# Copy only the built JAR from the previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create directories for logs and set correct permissions
RUN mkdir -p /var/log/eventhub && \
    chown -R eventapp:eventapp /var/log/eventhub && \
    chown -R eventapp:eventapp /app

# Switch to non-root user for security
USER eventapp

# Expose application port
EXPOSE 8080

# Set health check to ensure container is healthy
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Define entry point for the application with sensible JVM options
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=70", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "/app/app.jar"]