plugins {
    id("java-library")
}

description = "payment"
group = "traintickets.payment"

dependencies {
    api(project(":business-logic"))
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("payment")
    archiveClassifier.set("")
    archiveVersion.set("")
}