plugins {
    id("java-library")
}

description = "data-access"
group = "traintickets.dataaccess"

dependencies {
    api(project(":business-logic"))
    api(project(":jdbc"))
    implementation("org.postgresql:postgresql:42.7.5")
    testImplementation("org.testcontainers:postgresql:1.20.6")
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("data-access")
    archiveClassifier.set("")
    archiveVersion.set("")
}