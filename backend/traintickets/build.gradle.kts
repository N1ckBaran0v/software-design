plugins {
    id("java")
}

subprojects {
    apply(plugin = "java")

    group = "ru.traintickets"
    version = "1.0-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.register<Wrapper>("wrapper") {
        gradleVersion = "8.8"
    }

    tasks.register("prepareKotlinBuildScriptModel") {
    }
}