FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .

RUN mvn -q -DskipTests -pl phase3-site-generator -am package

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /build/phase3-site-generator/target/phase3-site-generator-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
