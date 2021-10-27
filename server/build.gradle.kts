val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
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

    // Encryption
    implementation("org.mindrot:jbcrypt:0.4")

    // JSON Schema validation
    implementation("org.everit.json:org.everit.json.schema:1.5.1")

    // Ktor dependencies
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.example.social_compose.server.ApplicationKt"))
        }
    }
}
