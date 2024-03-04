import com.epages.restdocs.apispec.gradle.OpenApi3Task

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.github.earlgrey02:JWT:1.0.0")
    implementation("net.nurigo:sdk:4.3.0")
    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":domain")))
}

tasks {
    test {
        finalizedBy(withType<OpenApi3Task>())
    }

    jar {
        enabled = false
    }

    bootJar {
        enabled = true
    }

    jib {
        from {
            image = "openjdk:17-oracle"
        }
        to {
            image = "san06036/doyoumate-api"
        }
    }

    openapi3 {
        setServer("DoyouMate API Docs")
        title = "DoyouMate API"
        version = "v1"
        format = "yaml"
        outputFileNamePrefix = "api"
        outputDirectory = "src/main/resources/static/docs"
    }
}
