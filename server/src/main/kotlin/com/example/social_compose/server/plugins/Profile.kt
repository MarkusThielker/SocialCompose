package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig.postDao
import com.example.social_compose.server.ServerConfig.userDao
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Resource("/{username}")
class Profile(val username: String) {

    @Serializable
    @Resource("/feed/{page?}")
    data class Posts(val profile: Profile, val page: Int = 0)
}

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
