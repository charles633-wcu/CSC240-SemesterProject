FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
EXPOSE 8081
CMD ["java", "-jar", "target/data-api-1.0-SNAPSHOT.jar"]
