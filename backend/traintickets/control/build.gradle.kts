plugins {
    id("java-library")
}

description = "control"
group = "ru.traintickets.control"

dependencies {
    api(project(":business-logic"))
    api(project(":di"))
    api(project(":data-access"))
    api(project(":security"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.traintickets.control.Main"
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("control")
    archiveClassifier.set("")
    archiveVersion.set("")
}