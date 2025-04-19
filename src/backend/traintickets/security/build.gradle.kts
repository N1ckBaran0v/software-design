plugins {
    id("java-library")
}

description = "security"
group = "traintickets.security"

dependencies {
    api(project(":business-logic"))
    implementation("redis.clients:jedis:5.2.0")
    implementation("com.auth0:java-jwt:4.5.0")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
}

tasks.jar {
    manifest {
        attributes["Created-By"] = "Nikolay Baranov"
    }
    archiveBaseName.set("security")
    archiveClassifier.set("")
    archiveVersion.set("")
}