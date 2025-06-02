# Step 1: Build the Spring Boot application
FROM gradle:8.10-jdk17 AS builder

WORKDIR /app

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon --refresh-dependencies || true

COPY src src

RUN ./gradlew bootJar --no-daemon

# Step 2: Create the final image (Debian 기반 openjdk)
FROM openjdk:17-buster

WORKDIR /app

RUN apt-get update && apt-get install -y iputils-ping dnsutils && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 4400

CMD ["java", "-jar", "app.jar"]
