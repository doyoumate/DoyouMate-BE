dependencies {
    implementation(project(":common"))
    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    testFixturesImplementation("com.github.earlgrey02:JWT:1.0.0")
}
