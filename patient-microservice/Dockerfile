FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code
COPY src src

# Build the application
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# Expose the port
EXPOSE 9101

# Run the application using wildcard to handle version changes
ENTRYPOINT ["sh", "-c", "exec java -jar target/patient-microservice-*.jar"]


