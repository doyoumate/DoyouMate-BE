dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
}

tasks {
    jar {
        enabled = false
    }

    bootJar {
        enabled = true
    }
}
