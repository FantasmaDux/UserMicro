FROM gradle:8.7-jdk21 AS build

WORKDIR /home/gradle/project

# 1. Копируем файлы конфигурации Gradle
COPY --chown=gradle:gradle settings.gradle.kts build.gradle.kts ./
COPY --chown=gradle:gradle gradle ./gradle

# 2. Кэшируем зависимости
RUN gradle --no-daemon dependencies || true

# 3. Копируем исходники
COPY --chown=gradle:gradle src ./src

# 4. Собираем JAR
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/*.jar networking-micro.jar

ENTRYPOINT ["java", "-jar", "networking-micro.jar"]

EXPOSE 8080
EXPOSE 9090