package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig.postDao
import com.example.social_compose.server.ServerConfig.userDao
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@KtorExperimentalLocationsAPI
@Location("/{username}")
class Profile(val username: String) {

    @Location("/feed/{page?}")
    data class Posts(val profile: Profile, val page: Int = 0)
}

@KtorExperimentalLocationsAPI
@ExperimentalSerializationApi
fun Application.configureProfile() {

    routing {

        get<Profile> { profile ->

            userDao.getUserByUsername(profile.username)?.let {
                call.respondText(
                    text = Json.encodeToString(it.toPublicUser()),
                    contentType = ContentType.Application.Json,
                )
                return@get
            }

            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Username not found",
            )
        }

        get<Profile.Posts> { posts ->

            val userPosts = postDao.getPostsByIndex(index = posts.page, username = posts.profile.username)
            call.respondText(
                text = Json.encodeToString(userPosts),
                contentType = ContentType.Application.Json,
            )
        }
    }
}
