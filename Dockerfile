# --- Stage 1: Build the application ---
FROM maven:3.8.6-openjdk-21-slim AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and settings.xml (if you have one)
COPY pom.xml ./

# Download dependencies (this leverages Docker layer caching)
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application (native or JVM)
#  *  -Dnative  :  Uncomment if you want to build a native executable (requires GraalVM)
#  *  -DskipTests :  Skips running tests during the build process.  Good for smaller images and faster builds.
#  *  -Dquarkus.container-image.build=true :  Tells Quarkus to use the container image extension.
#  *  -Dquarkus.container-image.push=false : Prevents pushing the image to a registry during the build.
#  *  -Dquarkus.profile=prod:  Specifies the "prod" profile, which is often used for production builds.
RUN mvn package -DskipTests -Dquarkus.container-image.build=true -Dquarkus.container-image.push=false

# --- Stage 2: Create the runtime image ---
# Choose a base image appropriate for your architecture.  For Raspberry Pi, pick an ARM-based image.
#  *  Eclipse Temurin is a good choice.  Pick a slim version to reduce image size.
#  *  Be sure to select the ARM version.  Replace `linux/amd64` with `linux/arm64` if your Pi is 64-bit.  Use `linux/arm/v7` if it's a 32-bit Pi.
FROM eclipse-temurin:21-jre-focal AS runner

# Create a non-root user
RUN addgroup --system quarkus && adduser --system --ingroup quarkus quarkus

# Set the working directory
WORKDIR /app

# Copy the application artifact from the builder stage
COPY --from=builder /app/target/*-runner.jar ./application.jar

# Set the user to run the application
USER quarkus

# Expose the port your application listens on (typically 8080 or 8081)
EXPOSE 8080

# Set environment variables (if needed)
# ENV SOME_VARIABLE=some_value

# Run the application
ENTRYPOINT ["java", "-jar", "application.jar"]