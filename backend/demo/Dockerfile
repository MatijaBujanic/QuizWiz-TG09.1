# Use Java 21
FROM eclipse-temurin:21-jdk-alpine as builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code
COPY src src

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Create runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR file
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Railway will set PORT environment variable)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]