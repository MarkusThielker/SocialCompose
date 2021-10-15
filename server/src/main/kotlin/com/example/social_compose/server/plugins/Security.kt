package com.example.social_compose.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.social_compose.server.ServerConfig.issuer
import com.example.social_compose.server.ServerConfig.secret
import com.example.social_compose.server.ServerConfig.userDao
import com.example.social_compose.server.ServerConfig.validity
import com.example.social_compose.shared.AuthCredentials
import com.example.social_compose.shared.AuthToken
import com.example.social_compose.shared.user.UserRegistration
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.sql.Date
import java.sql.Timestamp

@ExperimentalSerializationApi
fun Application.configureSecurity() {

    authentication {

        jwt("jwt-auth") {

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

        post("api/v1/user/create") {

            val content = call.receiveText()
            val userRegistration = Json.decodeFromString<UserRegistration>(content)

            val file = File("src/main/resources/registration.schema.json")
            val jsonTokener = JSONTokener(file.bufferedReader())
            val jsonObject = JSONObject(jsonTokener)
            val schema = SchemaLoader.load(jsonObject)

            try {

                // check for valid format
                schema.validate(JSONObject(content))

                // check for duplicate username
                if (userDao.usernameTaken(userRegistration.username)) {
                    call.respond(HttpStatusCode.Conflict, "Username is already used by another account")
                    return@post
                }

                // check for duplicate email
                if (userDao.emailTaken(userRegistration.email)) {
                    call.respond(HttpStatusCode.Conflict, "Email is already used by another account")
                    return@post
                }

                // add item to database
                userDao.createUser(userRegistration)

                // check for created item
                if (userDao.usernameTaken(userRegistration.username) && userDao.emailTaken(userRegistration.email))
                    call.respond(HttpStatusCode.Accepted, "User has been created")
                else
                    call.respond(HttpStatusCode.InternalServerError, "User couldn't be created")

            } catch (ve: ValidationException) {

                call.respond(
                    HttpStatusCode.Conflict,
                    "The data sent for registration doesn't match the format requirements"
                )
            }
        }

        post("/api/v1/user/authenticate") {

            val content = call.receiveText()
            val credentials = Json.decodeFromString<AuthCredentials>(content)

            val user = userDao.getUserByUsername(credentials.username)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Username not found")
                return@post
            }

            val isValid = BCrypt.checkpw(credentials.password, user.password)
            if (!isValid) {
                call.respond(HttpStatusCode.Unauthorized, "Password not correct")
                return@post
            }

            val token = JWT.create()
                .withIssuer(issuer)
                .withClaim("userId", user.userId)
                .withClaim("username", user.username)
                .withIssuedAt(Date(System.currentTimeMillis()))
                .withExpiresAt(Date(System.currentTimeMillis() + validity))
                .sign(Algorithm.HMAC256(secret))

            call.respondText(Json.encodeToString(AuthToken(token)), ContentType.Application.Json)
        }

        authenticate("jwt-auth") {

            get("/api/v1/user/token-status") {

                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val issuedAt = principal.issuedAt?.time
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())

                if (issuedAt != null && expiresAt != null)
                    call.respondText(
                        "Hello, $username! Your token was issued at ${Timestamp(issuedAt)} and will expire " +
                                "in ${(expiresAt / 1000) / 60} minutes and ${(expiresAt / 1000) % 60} seconds"
                    )
                else
                    call.respondText { "Something with the times stored in the token went wrong" }
            }
        }
    }
}
