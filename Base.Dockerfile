# Stage 1: Build
FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /app

ARG SERVICE_SRC
ARG JAR_NAME

# Copy pom.xml and download dependencies
COPY ${SERVICE_SRC}/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copy application.properties (or .yaml) to invalidate cache if config changes
COPY ${SERVICE_SRC}/src/main/resources/application.* ./src/main/resources/

# Copy the source code
COPY ${SERVICE_SRC}/src ./src

# Build the application
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:17
WORKDIR /app

ARG JAR_NAME
ARG EXPOSE_PORT

# Copy the built JAR from builder stage
COPY --from=builder /app/target/${JAR_NAME} app.jar

# Expose the port passed as argument
EXPOSE ${EXPOSE_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]