package com.example.social_compose.server

import com.example.social_compose.server.ServerConfig.host
import com.example.social_compose.server.ServerConfig.port
import com.example.social_compose.server.plugins.configureDatabase
import com.example.social_compose.server.plugins.configureSecurity
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@ExperimentalSerializationApi
fun main(args: Array<String>) {

    embeddedServer(Netty, port, host) {

        install(ContentNegotiation) {
            Json {
                prettyPrint = true
                isLenient = true
            }
        }

        configureDatabase()
        configureSecurity()

    }.start(wait = true)
}
