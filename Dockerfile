# =========================
# Build stage
# =========================
FROM gradle:8.7-jdk17-alpine AS build

WORKDIR /app

# Copy only build files first (better cache)
COPY gradle gradle
COPY gradlew gradlew
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copy source
COPY src src

# Build fat jar
RUN ./gradlew bootJar --no-daemon

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Render sets PORT dynamically
EXPOSE 8080

# JVM flags optimized for containers
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]