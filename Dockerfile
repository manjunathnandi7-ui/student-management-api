FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the Maven build output (JAR file)
COPY target/*.jar app.jar

# Expose the default port (Render will override with $PORT)
EXPOSE 8080

# Run the application
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT:8080}"]
