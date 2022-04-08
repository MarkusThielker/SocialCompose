val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "com.example.social_compose.server"
version = "1.0"

application {
    mainClass.set("com.example.social_compose.server.ApplicationKt")
}

dependencies {

    implementation(project(":shared"))

    // Database dependencies
    implementation("org.jetbrains.exposed:exposed:0.17.14")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.4")
    implementation("mysql:mysql-connector-java:8.0.26")

    // HTML
    implementation("io.ktor:ktor-server-freemarker:$ktorVersion")

    // Encryption
    implementation("org.mindrot:jbcrypt:0.4")

    // JSON Schema validation
    implementation("org.everit.json:org.everit.json.schema:1.5.1")

    // Ktor core
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Ktor authentication
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")

    // Ktor logging
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Ktor testing
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$ktorVersion")

}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.example.social_compose.server.ApplicationKt"))
        }
    }
}
