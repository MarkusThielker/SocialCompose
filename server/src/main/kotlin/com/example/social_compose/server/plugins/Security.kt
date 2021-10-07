package com.example.social_compose.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.social_compose.server.ServerConfig.issuer
import com.example.social_compose.server.ServerConfig.secret
import com.example.social_compose.server.ServerConfig.validity
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ExperimentalSerializationApi
fun Application.configureSecurity() {

    authentication {

        jwt {

            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .acceptIssuedAt(System.currentTimeMillis())
                    .acceptExpiresAt(System.currentTimeMillis() + validity)
                    .build()
            )

            validate { credential ->
                if (credential.payload.issuer == issuer)
                    JWTPrincipal(credential.payload)
                else
                    null
            }
        }
    }

    routing {

        get("/") {
            call.respondText(ContentType.Application.Json) {
                Json.encodeToString(mapOf("message" to "Hello, world!"))
            }
        }
    }
}
