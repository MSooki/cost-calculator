# Build stage (only Java)
FROM azul/zulu-openjdk:21-latest as build

# Set working directory
WORKDIR /app

# Copy all project files (including already-copied frontend build)
COPY . .

# Build Spring Boot app
RUN ./gradlew clean build

# Run stage
FROM azul/zulu-openjdk:21-latest

# Set working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/calculator-0.0.1-SNAPSHOT.jar app.jar

# Expose the app port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
