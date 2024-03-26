plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("jacoco")
    id("org.sonarqube") version "5.0.0.4638"
}

group = "com.helmes"
version = "0.0.1-SNAPSHOT"
val springdocOpenApiVersion = "2.4.0"
val selenideVersion = "7.2.2"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
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

val isE2ETestEnvironmentEnabled = System.getenv("E2E")?.toBoolean() == true

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    if (isE2ETestEnvironmentEnabled) {
        configureE2ETestEnvironment()
    } else {
        exclude("**/e2e/**")
    }
    finalizedBy(":jacocoTestReport")
}

fun Test.configureE2ETestEnvironment() {
    include("**/e2e/**")
    val properties = mutableMapOf(
        "webdriver.chrome.logfile" to "build/reports/chromedriver.log",
        "webdriver.chrome.verboseLogging" to "true"
    )
    if (project.hasProperty("headless")) {
        properties["chromeoptions.args"] = "--headless,--no-sandbox,--disable-gpu"
    }
    systemProperties.putAll(properties)
}

val skipJacoco: Boolean = false
val jacocoEnabled: Boolean = true
tasks.withType<JacocoReport> {
    isEnabled = jacocoEnabled
    if (skipJacoco) {
        enabled = false
    }
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

sonar {
    properties {
        property("sonar.projectKey", "sectors")
        property("sonar.organization", "ktenman")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.exclusions", "src/test/**")
    }
}

