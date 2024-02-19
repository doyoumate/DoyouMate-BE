dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.h2database:h2")
    implementation("com.github.earlgrey02:JWT:1.0.0")
}

tasks {
    jar {
        enabled = false
    }

    bootJar {
        enabled = true
    }
}
