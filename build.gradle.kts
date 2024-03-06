plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.helmes"
version = "0.0.1-SNAPSHOT"
val springdocOpenApiVersion = "2.3.0"
val selenideVersion = "7.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocOpenApiVersion")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.codeborne:selenide:$selenideVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()

    if (System.getenv("E2E")?.toBoolean() == true) {
        include("**/e2e/**")
        systemProperties["webdriver.chrome.logfile"] = "build/reports/chromedriver.log"
        systemProperties["webdriver.chrome.verboseLogging"] = "true"
        if (project.hasProperty("headless")) {
            systemProperties["chromeoptions.args"] = "--headless,--no-sandbox,--disable-gpu"
        }
    } else {
		exclude("**/e2e/**")
	}
}
