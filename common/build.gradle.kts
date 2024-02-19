dependencies {
    api("org.springframework.boot:spring-boot-starter-aop")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    testFixturesImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testFixturesImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testFixturesImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    testFixturesImplementation("io.mockk:mockk:1.13.5")
    testFixturesImplementation("io.projectreactor:reactor-test")
    testFixturesImplementation("com.ninja-squad:springmockk:3.0.1")
    testFixturesImplementation("com.epages:restdocs-api-spec-webtestclient:0.17.1")
    testFixturesImplementation("com.epages:restdocs-api-spec:0.17.1")
}
