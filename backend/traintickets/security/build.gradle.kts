plugins {
    id("java-library")
}

description = "security"
group = "ru.traintickets.security"

dependencies {
    api(project(":business-logic"))
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("security")
    archiveClassifier.set("")
    archiveVersion.set("")
}