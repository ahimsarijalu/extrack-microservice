# Stage 1: Base image for Maven dependencies
FROM maven:3.9.9-amazoncorretto-21 AS base

# Set the working directory
WORKDIR /app

# Copy the pom.xml file and download dependencies only (cache step)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Stage 2: Build the application
FROM base AS builder

# Copy the source code to the container
COPY src ./src

# Build the application (this will generate the .jar file in the target/ directory)
RUN mvn package -DskipTests

# Stage 3: Runtime image
FROM openjdk:21

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built .jar file from the builder stage
COPY --from=builder /app/target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "auth-service.jar"]