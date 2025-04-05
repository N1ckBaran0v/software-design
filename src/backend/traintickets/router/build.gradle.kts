plugins {
    id("java-library")
}

description = "router"
group = "traintickets.router"

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("router")
    archiveClassifier.set("")
    archiveVersion.set("")
}