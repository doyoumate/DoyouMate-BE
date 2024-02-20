dependencies {
    implementation(project(":common"))
    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    api("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    testFixturesImplementation("org.springframework.security:spring-security-test")
    testFixturesImplementation("com.github.earlgrey02:JWT:1.0.0")
}
