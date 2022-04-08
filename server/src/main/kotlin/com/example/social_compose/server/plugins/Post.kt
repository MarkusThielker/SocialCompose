package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig
import com.example.social_compose.shared.post.PostPost
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveText
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Resource("/{username?}/{page?}")
data class Root(val username: String?, val page: Int?)

@Serializable
@Resource("/post/{postId}")
data class Post(val postId: Long)

fun Application.configurePost() {

    routing {

        authenticate("jwt-auth") {

            /**
             * Process calls to the [root route][Root].
             * The response is a list of posts, optionally filtered for the passed user.
             *
             * @author Markus Thielker
             *
             * */
            get<Root> { request ->

                val userPosts = ServerConfig.postDao.getPostsByIndex(
                    index = (request.page ?: 0) * 10,
                    username = request.username ?: "",
                )
                call.respondText(
                    text = Json.encodeToString(userPosts),
                    contentType = ContentType.Application.Json,
                )
            }

            /**
             * Processes calls to the [post route][Post].
             * The request contains the postId.
             *
             * @author Markus Thielker
             *
             * */
            get<Post> { request ->

                val post = ServerConfig.postDao.getPostById(request.postId)
                call.respondText(
                    text = Json.encodeToString(post),
                    contentType = ContentType.Application.Json,
                )
            }

            /**
             * Processes calls to the "create post" api endpoint.
             * Confirms validity of the passed json object and inserts it into db if valid.
             *
             * @author Markus Thielker
             *
             * */
            post("/api/v1/post/create") {

                // read json content and parse to post object
                val json = call.receiveText()
                val post = Json.decodeFromString<PostPost>(json)

                // read jwt principal and get tokens userId
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asLong()

                // cancel and return if parentId is invalid
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

                // cancel and return if post is too long
                if (post.content.length > 280) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "The content of the post is too long (max length: 280 characters)",
                    )
                    return@post
                }

                // insert post into database
                ServerConfig.postDao.createPost(post)

                // respond with success
                call.respond(
                    status = HttpStatusCode.Accepted,
                    message = "Post has been created",
                )
            }

        }
    }
}
