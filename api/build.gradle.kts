import com.epages.restdocs.apispec.gradle.OpenApi3Task

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
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
}

openapi3 {
    setServer("DoyouMate API Docs")
    title = "DoyouMate API"
    version = "v1"
    format = "yaml"
    outputFileNamePrefix = "api"
    outputDirectory = "src/main/resources/static/docs"
}
