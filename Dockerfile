# Stage 1: Build stage (optional, if you're building from source)
# If your build is already done and you have the JAR file, you can skip this stage
# You need to have a Docker multi-stage build setup.

# Use Maven to build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the source code to the container
COPY pom.xml ./
COPY src ./src

# Stage 2: Production stage

# Use an OpenJDK image as the base image for production
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY target/scrumble-0.0.1-SNAPSHOT.jar app.jar

# Alternatively, if you already have the JAR file built locally, use this:
# COPY target/your-app-name-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional, usually Spring Boot runs on port 8080)
EXPOSE 8080

# Entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
