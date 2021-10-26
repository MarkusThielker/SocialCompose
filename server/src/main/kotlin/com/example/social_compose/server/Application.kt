package com.example.social_compose.server

import com.example.social_compose.server.ServerConfig.host
import com.example.social_compose.server.ServerConfig.port
import com.example.social_compose.server.plugins.configureDatabase
import com.example.social_compose.server.plugins.configurePost
import com.example.social_compose.server.plugins.configureProfile
import com.example.social_compose.server.plugins.configureSecurity
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

@KtorExperimentalLocationsAPI
@ExperimentalSerializationApi
fun main(args: Array<String>) {

    embeddedServer(Netty, port, host) {

        install(ContentNegotiation) {
            Json {
                prettyPrint = true
                isLenient = true
            }
        }

        install(CallLogging) {
            level = Level.INFO

            format { call ->
                val route = call.request.uri
                val httpMethod = call.request.httpMethod.value
                val status = call.response.status()
                "Route: $route, HTTP method: $httpMethod, Status: $status"
            }
        }

        install(Locations)

        configureDatabase()
        configureSecurity()
        configureProfile()
        configurePost()

    }.start(wait = true)
}
