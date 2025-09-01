# Base image: Java 17
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Arg for jar file (Gradle build output)
ARG JAR_FILE=build/libs/*.jar

# Copy jar to workdir
COPY ${JAR_FILE} app.jar

# Expose port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
