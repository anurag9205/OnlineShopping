# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/OnlineShoppingWithReview-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "app.jar"]
