# ----------- STAGE 1: Build the Quarkus application -----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project definition file (pom.xml)
COPY pom.xml .

# Download dependencies (this speeds up builds by caching dependencies)
RUN mvn dependency:go-offline -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

# Copy the source code
COPY src ./src

# Build the Quarkus application (native image)
RUN mvn package -Dnative -Dquarkus.native.container-build=true -DskipTests=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

# ----------- STAGE 2: Create the runtime image -----------
FROM registry.access.redhat.com/ubi8/ubi-minimal:latest AS runner

# Set the working directory
WORKDIR /app

# Copy the application from the builder stage
COPY --from=builder /app/target/*-runner /app/application

# Expose the port your application listens on (default is 8080)
EXPOSE 8090

# Set the user to run the application as (optional, but recommended for security)
USER 1001

# Run the application
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]