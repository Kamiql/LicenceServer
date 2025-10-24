plugins {
    kotlin("jvm") version "2.3.0-Beta1"
    kotlin("plugin.serialization") version "2.3.0-Beta1"
    id("io.ktor.plugin") version "3.3.1"
    id("com.gradleup.shadow") version "9.2.2"
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
    implementation("io.ktor:ktor-serialization-gson")

    implementation("org.litote.kmongo:kmongo:4.10.0")

    implementation("ch.qos.logback:logback-classic:1.5.13")

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
}

kotlin {
    jvmToolchain(21)
}

tasks.shadowJar {
    archiveFileName = "backend.jar"
}