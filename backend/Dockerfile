# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Cache dependencies first (pom rarely changes relative to source)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Build the application
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Governed source PDFs used by the form-automation feature (app.forms.source-dir=./forms/source)
COPY forms ./forms

# Fat jar produced by the build stage
COPY --from=build /build/target/*.jar app.jar

# Uploaded/generated documents live here (mounted as a volume in docker-compose)
RUN mkdir -p /app/uploads
VOLUME ["/app/uploads"]

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
