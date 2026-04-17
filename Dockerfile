# Multi-stage build for Spring Boot application

# Stage 1: Build stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle wrapper and configuration files
COPY gradle ./gradle
COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle.properties* ./

# Fix Windows CRLF line endings and set execute permission
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build application
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jdk-noble

WORKDIR /app

# Create user for running application (security best practice)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Install required packages and Korean locale
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    fontconfig \
    locales \
    tzdata \
    telnet \
    && locale-gen ko_KR.UTF-8 \
    && rm -rf /var/lib/apt/lists/*

# Set locale and timezone
ENV LANG=ko_KR.UTF-8 \
    LANGUAGE=ko_KR:ko \
    LC_ALL=ko_KR.UTF-8 \
    TZ=Asia/Seoul

# GPKI API 환경변수
ENV GPKI_HOME="/home/aram/vol/gpki" \
    JAVA_HOME="/opt/java/openjdk" \
    CLASSPATH="/home/aram/vol/gpki/jar/libgpkiapi_jni.jar" \
    LD_LIBRARY_PATH="/home/aram/vol/gpki/lib64/" \
    LIBPATH="/home/aram/vol/gpki/lib64/"
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create directory for file uploads
RUN mkdir -p /home/aram && \
    chown -R appuser:appuser /home/aram && \
    chmod 755 /home/aram

# Change ownership of application files
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
