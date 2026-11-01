plugins {
    `java-library`
    checkstyle
    id("com.diffplug.spotless") version "8.10.4"
}

group = "org.metabolomicshub.jsonprofile"
version = "0.1.0"

layout.buildDirectory = layout.projectDirectory.dir("../.cache/java/build")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat("1.36.1")
    }
}

checkstyle {
    toolVersion = "14.4.0"
    config = resources.text.fromArchiveEntry(
        configurations.checkstyle.get().filter { it.name.startsWith("checkstyle-") }.singleFile,
        "google_checks.xml",
    )
    configProperties = mapOf(
        "org.checkstyle.google.suppressionfilter.config" to
            file("config/checkstyle/checkstyle-suppressions.xml").absolutePath,
    )
}

tasks.withType<org.gradle.api.plugins.quality.Checkstyle>().configureEach {
    maxWarnings = 0
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")
    implementation("com.networknt:json-schema-validator:1.5.6")
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")
    implementation("org.slf4j:slf4j-api:2.0.17")

    // Optional OPA WASM support
    compileOnly("com.dylibso.chicory:runtime:1.1.1")
    compileOnly("org.apache.commons:commons-compress:1.27.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("com.dylibso.chicory:runtime:1.1.1")
    testImplementation("org.apache.commons:commons-compress:1.27.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
}

tasks.test {
    useJUnitPlatform()
}
