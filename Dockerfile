# Stage 1: Build the application using a Gradle image
FROM gradle:jdk21 AS builder
LABEL maintainer="EventHub Team <info@eventhub.com>"
LABEL stage="builder"

WORKDIR /app

# Copy necessary build files first
COPY build.gradle.kts settings.gradle.kts ./

# --- Keep the wrapper generation workaround for now ---
# Generate the wrapper files inside the container instead of copying
RUN gradle wrapper
# Ensure the generated gradlew script is executable
RUN chmod +x ./gradlew
# --- End workaround ---

# Copy the source code NOW, before building
COPY src ./src

# Build the application JAR (only run bootJar once)
# Skips tests to speed up image build, tests should run in CI
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Create the runtime image using a JRE image
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="EventHub Team <info@eventhub.com>"
LABEL version="1.0"
LABEL description="EventHub Event Service Microservice"

# Create a non-root user to run the application
RUN addgroup -S eventapp && adduser -S eventapp -G eventapp

WORKDIR /app

# Copy only the built JAR from the builder stage
# The JAR is created in the '/app/build/libs/' directory in the builder stage
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