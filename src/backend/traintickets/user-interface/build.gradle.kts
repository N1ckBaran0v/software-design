plugins {
    id("java-library")
}

description = "user-interface"
group = "traintickets.ui"

dependencies {
    api(project(":business-logic"))
    implementation("com.google.code.gson:gson:2.12.1")
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("user-interface")
    archiveClassifier.set("")
    archiveVersion.set("")
}