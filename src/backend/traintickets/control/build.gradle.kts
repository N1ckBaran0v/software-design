plugins {
    id("java-library")
}

description = "control"
group = "traintickets.control"

dependencies {
    api(project(":business-logic"))
    api(project(":di"))
    api(project(":data-access"))
    api(project(":jdbc"))
    api(project(":payment"))
    api(project(":security"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "traintickets.control.Main"
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("control")
    archiveClassifier.set("")
    archiveVersion.set("")
}