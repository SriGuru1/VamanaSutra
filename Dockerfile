# Stage 1: Build the Vite frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build the Spring Boot backend
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Create the static resources directory if it doesn't exist
RUN mkdir -p src/main/resources/static
# Copy the built frontend files to the backend static resources
COPY --from=frontend-build /app/frontend/dist/ ./src/main/resources/static/
RUN mvn clean package -DskipTests

# Stage 3: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
# Expose the port defined in application.properties
EXPOSE 5000
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-5000} -jar app.jar"]
