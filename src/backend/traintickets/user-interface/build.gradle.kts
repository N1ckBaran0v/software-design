plugins {
    id("java-library")
}

description = "user-interface"
group = "traintickets.ui"

dependencies {
    api(project(":business-logic"))
    api(project(":payment"))
    api(project(":security"))
    implementation("io.javalin:javalin:6.6.0")
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