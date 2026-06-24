
# Stage 1: Build the Maven application
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
# Prefetch dependencies to leverage Docker cache layers
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the built jar from the builder stage
COPY --from=builder /app/target/payment-service-0.0.1-SNAPSHOT.jar app.jar
# Expose the application port configured in application.properties
EXPOSE 8083
# Run the application with production flags
ENTRYPOINT ["java", "-jar", "app.jar"]
