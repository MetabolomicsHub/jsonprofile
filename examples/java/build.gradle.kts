plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.metabolomicshub.jsonprofile:jsonprofile:0.1.0")

    runtimeOnly("com.dylibso.chicory:runtime:1.1.1")
    runtimeOnly("org.apache.commons:commons-compress:1.27.1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.metabolomicshub.mztabm.validation.MzTabMProfileValidator"
}
