FROM maven:3.9.9-eclipse-temurin-11 AS build

WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:11-jre-jammy AS runtime

WORKDIR /app

COPY --from=build --chown=10001:10001 \
  /workspace/target/api-spring-0.0.1-SNAPSHOT.jar /app/app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]

