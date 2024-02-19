plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.epages.restdocs-api-spec") version "0.17.1"
    id("java-library")
    id("java-test-fixtures")
    id("maven-publish")
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

allprojects {
    group = "com.doyoumate"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("com.epages.restdocs-api-spec")
        plugin("java-library")
        plugin("java-test-fixtures")
        plugin("maven-publish")
    }

    configurations {
        all {
            exclude("org.springframework.boot", "spring-boot-starter-logging")
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-log4j2")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("io.github.microutils:kotlin-logging:3.0.5")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
        testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
        testImplementation("io.kotest:kotest-assertions-core:5.6.2")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
        testImplementation("io.mockk:mockk:1.13.5")
        testImplementation("io.projectreactor:reactor-test")
        testImplementation("com.ninja-squad:springmockk:3.0.1")
        testImplementation("com.epages:restdocs-api-spec-webtestclient:0.17.1")
        testImplementation("com.epages:restdocs-api-spec:0.17.1")
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                freeCompilerArgs += "-Xjsr305=strict"
                jvmTarget = "17"
            }
        }

        jar {
            enabled = true
        }

        bootJar {
            enabled = false
        }

        test {
            useJUnitPlatform()
        }
    }
}
