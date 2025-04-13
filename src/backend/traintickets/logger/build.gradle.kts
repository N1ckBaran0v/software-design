plugins {
    id("java-library")
}

description = "logger"
group = "traintickets.logger"

dependencies {
    api(project(":business-logic"))
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:log4j-over-slf4j:2.0.16")
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("logger")
    archiveClassifier.set("")
    archiveVersion.set("")
}