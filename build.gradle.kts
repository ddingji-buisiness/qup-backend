plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.qup"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.retry:spring-retry")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.6.0")

    // Database Migration
    // implementation("org.flywaydb:flyway-core")
    // implementation("org.flywaydb:flyway-mysql")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    // AWS S3
    implementation(platform("com.amazonaws:aws-java-sdk-bom:1.12.763"))
    implementation("com.amazonaws:aws-java-sdk-s3")

    // Firebase
    implementation("com.google.firebase:firebase-admin:9.2.0")

    // Logging & Monitoring
    implementation("ch.qos.logback:logback-classic")
    implementation("org.slf4j:slf4j-api")

    // Rate Limiting
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-redis:7.6.0")

    // Utilities
    implementation("com.opencsv:opencsv:5.7.1")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.apache.tika:tika-core:3.0.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("net.datafaker:datafaker:2.4.2")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // Database Drivers
    runtimeOnly("com.mysql:mysql-connector-j")

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")

    // Annotation Processors
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
