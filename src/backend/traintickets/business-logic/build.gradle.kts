plugins {
    id("java-library")
}

description = "business-logic"
group = "traintickets.businesslogic"

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("business-logic")
    archiveClassifier.set("")
    archiveVersion.set("")
}