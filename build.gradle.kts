import com.google.protobuf.gradle.id

plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
    id("org.openapi.generator") version "7.3.0"
}

group = "io.github.pavelshe11"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springGrpcVersion"] = "0.9.0"

dependencies {
    // Для работы с бд
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Валидация dto через @Valid
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Веб-приложения
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Сокеты для работы в реальном времени
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Для работы с grpc
    implementation("io.grpc:grpc-services")
    implementation ("io.grpc:grpc-protobuf:1.73.0")
    implementation ("io.grpc:grpc-stub:1.73.0")
    // Интеграция спринга и grpc
    implementation("net.devh:grpc-client-spring-boot-starter:2.15.0.RELEASE")
    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")
    // Для работы с сессиями бд
    implementation("org.springframework.session:spring-session-jdbc")
    // Генерация данных с regex
    implementation("com.github.curious-odd-man:rgxgen:3.0")
    // Для аутентификации и авторизации
    implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    // Сваггер документация
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
    // Парсинг и анализ файлов
    implementation("org.apache.tika:tika-core:3.2.2")
    implementation("org.apache.tika:tika-parsers-standard-package:3.2.2")
    // Для валидации ссылок и общих данных
    implementation ("commons-validator:commons-validator:1.7")
    // Для валидации номера телефона
    implementation ("com.googlecode.libphonenumber:libphonenumber:8.13.30")
    // for quartz
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    // Для парсинга xlsx
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // Для генерации фейковых данных
    implementation ("net.datafaker:datafaker:2.5.0")

    compileOnly("org.projectlombok:lombok")
    compileOnly ("org.apache.tomcat:annotations-api:6.0.53")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly ("io.grpc:grpc-netty-shaded:1.73.0")
    annotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("@generated=omit")
                }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}