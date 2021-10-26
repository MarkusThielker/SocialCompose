package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig
import com.example.social_compose.shared.post.PostPost
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@KtorExperimentalLocationsAPI
@Location("/post/{postId}")
data class Post(val postId: Long)

@KtorExperimentalLocationsAPI
@ExperimentalSerializationApi
fun Application.configurePost() {

    routing {

        authenticate("jwt-auth") {

            get("/") {

                val userPosts = ServerConfig.postDao.getPostsByIndex()
                call.respondText(
                    text = Json.encodeToString(userPosts),
                    contentType = ContentType.Application.Json,
                )
            }

            get<Post> { request ->

                val post = ServerConfig.postDao.getPostById(request.postId)
                call.respondText(
                    text = Json.encodeToString(post),
                    contentType = ContentType.Application.Json,
                )
            }

            post("/api/v1/post/create") {

                val json = call.receiveText()
                val post = Json.decodeFromString<PostPost>(json)

                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asLong()

                if (post.parentId != null && !ServerConfig.postDao.isValidPostId(post.parentId!!)) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "A post with postId ${post.parentId} does not exist",
                    )
                    return@post
                }

                if (userId == null || post.userId != userId) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Your are not allowed to post with another identity",
                    )
                    return@post
                }

                if (post.content.length > 280) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "The content of the post is too long (max length: 280 characters)",
                    )
                    return@post
                }

                ServerConfig.postDao.createPost(post)

                call.respond(
                    status = HttpStatusCode.Accepted,
                    message = "Post has been created",
                )
            }

        }
    }
}
