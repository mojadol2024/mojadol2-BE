# Step 1: Build the Spring Boot application
FROM gradle:8.10-jdk17 AS builder

# Set the working directory
WORKDIR /app

# Copy only the Gradle build files first to cache dependencies
COPY settings.gradle .
COPY build.gradle .

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle gradle

# Download dependencies (caches them for future builds)
RUN ./gradlew build --no-daemon --refresh-dependencies || return 0

# Copy the rest of the application source code
COPY src src

# Build the Spring Boot application using Gradle
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# Step 2: Create the final image
FROM openjdk:17

# Set the working directory
WORKDIR /app

# Copy the JAR file built in Step 1 into the container

RUN apt-get update && apt-get install -y iputils-ping dnsutils && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port that your Spring Boot application listens on
EXPOSE 4400

# Define the command to run your Spring Boot application
CMD ["java", "-jar", "app.jar"]
