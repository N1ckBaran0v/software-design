plugins {
    id("java-library")
}

description = "logger"
group = "traintickets.logger"

dependencies {
    api(project(":business-logic"))
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.slf4j:slf4j-simple:2.0.17")
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("logger")
    archiveClassifier.set("")
    archiveVersion.set("")
}