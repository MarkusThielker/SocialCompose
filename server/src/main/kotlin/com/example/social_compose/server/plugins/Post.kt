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
@Location("/{username?}/{page?}")
data class Root(val username: String?, val page: Int?)

@KtorExperimentalLocationsAPI
@Location("/post/{postId}")
data class Post(val postId: Long)

@KtorExperimentalLocationsAPI
@ExperimentalSerializationApi
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
                if (!ServerConfig.postDao.isValidPostId(post.parentId ?: -1)) {
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
