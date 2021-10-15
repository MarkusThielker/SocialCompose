val ktorVersion: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
}

group = "com.example.social_compose.shared"
version = "1.0"

dependencies {
    implementation("io.ktor:ktor-serialization:$ktorVersion")
}
