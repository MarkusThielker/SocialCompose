package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig.userDao
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureFeed() {

    routing {

        get("/") {

            call.respond(
                status = HttpStatusCode.Accepted,
                message = "Welcome to Social Compose \nThere are ${userDao.getUserCount()} users registered.",
            )
        }
    }
}
