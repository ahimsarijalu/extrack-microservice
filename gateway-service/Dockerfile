# Stage 1: Base image for Maven dependencies
FROM maven:3.9.9-amazoncorretto-17 AS base

# Set the working directory
WORKDIR /app

# Copy the pom.xml file and download dependencies only (cache step)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Stage 2: Build the application
FROM base AS builder

# Copy the source code to the container
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Stage 3: Runtime image
FROM maven:3.9.9-amazoncorretto-17

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built .jar file from the builder stage
COPY --from=builder /app/target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service-0.0.1-SNAPSHOT.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "gateway-service-0.0.1-SNAPSHOT.jar"]
