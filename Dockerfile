# Step 1: Build the Spring Boot application
FROM gradle:8.10-jdk17 AS builder

WORKDIR /app

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

RUN ./gradlew build --no-daemon --refresh-dependencies || return 0

COPY src src

RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# Step 2: Create the final image
FROM openjdk:17

WORKDIR /app

RUN apt-get update && apt-get install -y iputils-ping dnsutils && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 4400

CMD ["java", "-jar", "app.jar"]
