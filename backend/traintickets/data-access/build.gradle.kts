plugins {
    id("java-library")
}

description = "data-access"
group = "traintickets.dataaccess"

dependencies {
    api(project(":business-logic"))
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("data-access")
    archiveClassifier.set("")
    archiveVersion.set("")
}