configurations {
    all {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
}

tasks {
    jar {
        enabled = false
    }

    bootJar {
        enabled = true
    }
}
