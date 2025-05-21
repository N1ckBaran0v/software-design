plugins {
    id("java-library")
}

description = "data-access-mongo"
group = "traintickets.dataaccess.mongo"

dependencies {
    api(project(":business-logic"))
    implementation("org.mongodb:mongodb-driver-sync:5.5.0")
    testImplementation("org.testcontainers:mongodb:1.21.0")
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("data-access-mongo")
    archiveClassifier.set("")
    archiveVersion.set("")
}