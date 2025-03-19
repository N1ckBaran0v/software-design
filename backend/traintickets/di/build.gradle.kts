plugins {
    id("java-library")
}

description = "di"
group = "traintickets.di"

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("di")
    archiveClassifier.set("")
    archiveVersion.set("")
}