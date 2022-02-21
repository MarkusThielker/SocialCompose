package com.example.social_compose.server

import com.example.social_compose.server.plugins.configureDatabase
import com.example.social_compose.server.plugins.configurePost
import com.example.social_compose.server.plugins.configureProfile
import com.example.social_compose.server.plugins.configureSecurity
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@KtorExperimentalLocationsAPI
@ExperimentalSerializationApi
fun main() {

    val environment = applicationEngineEnvironment {

        connector {
            host = ServerConfig.host
            port = ServerConfig.port
        }

        module {

            install(ContentNegotiation) {

                Json {
                    prettyPrint = true
                    isLenient = true
                }
            }

            install(CallLogging) {

                level = org.slf4j.event.Level.INFO

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
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}
