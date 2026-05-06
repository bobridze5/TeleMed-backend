FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x ./gradlew
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
