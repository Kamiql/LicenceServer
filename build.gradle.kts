plugins {
    kotlin("multiplatform") version "2.3.0-Beta1" apply false
    id("io.ktor.plugin") version "3.3.1" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}