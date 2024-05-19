dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.github.earlgrey02:JWT:1.1.1")
    implementation("io.projectreactor.kafka:reactor-kafka")
    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":domain")))
}

tasks {
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
            image = "san06036/doyoumate-chat"
        }
    }
}
