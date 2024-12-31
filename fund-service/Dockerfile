# Stage 1: Build the application
FROM maven:3.9.9-amazoncorretto-21 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the source code to the container
COPY pom.xml .
COPY src ./src

# Build the application (this will generate the .jar file in the target/ directory)
RUN mvn clean install -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:21

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built .jar file from the builder stage to the runtime image
COPY --from=builder /app/target/fund-service-0.0.1-SNAPSHOT.jar fund-service.jar

# Expose the application port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "fund-service.jar"]