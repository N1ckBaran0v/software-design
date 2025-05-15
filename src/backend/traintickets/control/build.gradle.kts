plugins {
    id("java-library")
}

description = "control"
group = "traintickets.control"

dependencies {
    api(project(":business-logic"))
    api(project(":data-access-postgres"))
    api(project(":di"))
    api(project(":jdbc"))
    api(project(":logger"))
    api(project(":payment"))
    api(project(":security"))
    api(project(":user-interface"))
    implementation("org.yaml:snakeyaml:2.4")
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