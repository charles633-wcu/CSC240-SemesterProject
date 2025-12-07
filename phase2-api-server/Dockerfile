# =======================
# 1) Build entire project
# =======================
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Work in root of project
WORKDIR /build

# Copy ENTIRE repo — every module, parent pom, everything
COPY . .

# Build ONLY phase2-api-server, but also build its dependencies
RUN mvn -q -DskipTests -pl phase2-api-server -am package


# =======================
# 2) Runtime image
# =======================
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the fat jar produced by the Maven Shade plugin
COPY --from=build /build/phase2-api-server/target/phase2-api-server-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
