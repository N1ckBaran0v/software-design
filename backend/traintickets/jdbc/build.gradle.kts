plugins {
    id("java-library")
}

description = "jdbc"
group = "traintickets.jdbc"

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("jdbc")
    archiveClassifier.set("")
    archiveVersion.set("")
}