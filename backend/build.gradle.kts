plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.3.0-Beta1"
    id("io.ktor.plugin")
    application
}

group = "dev.kamiql"
version = "0.1.0"

application {
    mainClass.set("dev.kamiql.MainKt")
}

val ktorVersion = (project.properties["ktorVersion"] ?: "3.3.1").toString()
val koinVersion = (project.properties["koinVersion"] ?: "4.1.0").toString()

dependencies {
    implementation(platform("io.ktor:ktor-bom:$ktorVersion"))

    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation(project.dependencies.platform("io.insert-koin:koin-bom:$koinVersion"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-ktor")
    implementation("io.insert-koin:koin-logger-slf4j")
}

kotlin {
    jvmToolchain(21)
}
