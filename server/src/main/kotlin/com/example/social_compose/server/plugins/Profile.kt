package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig.postDao
import com.example.social_compose.server.ServerConfig.userDao
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
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
