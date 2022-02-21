package com.example.social_compose.server

import com.example.social_compose.server.plugins.configureDatabase
import com.example.social_compose.server.plugins.configureSecurity
import com.example.social_compose.shared.AuthCredentials
import com.example.social_compose.shared.AuthToken
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class AuthenticationTest {

    @Test
    fun testAuthenticationSuccess() {

        var callResponse: String

        val credentialsJson = Json.encodeToString(
            AuthCredentials.serializer(), AuthCredentials("MarkusT", "password")
        )

        val timestampPattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d*"
        val responsePattern = "Hello, [\\w]{3,16}! Your token was issued at $timestampPattern" +
                " and will expire in \\d* minutes and \\d* seconds"

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            handleRequest(HttpMethod.Post, "/api/v1/user/authenticate") {
                setBody(credentialsJson)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                callResponse = Json.decodeFromString<AuthToken>(response.content.toString()).token
                assert(callResponse.matches(Regex("\\S+.\\S+.\\S+")))
            }

            handleRequest(HttpMethod.Get, "/api/v1/user/token-status") {
                addHeader("Authorization", "Bearer $callResponse")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assert(response.content.toString().matches(Regex(responsePattern)))
            }
        }
    }

    @Test
    fun testAuthenticationFailedUsername() {

        val credentialsJson = Json.encodeToString(
            AuthCredentials.serializer(), AuthCredentials("NotMarkusT", "password")
        )

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            handleRequest(HttpMethod.Post, "/api/v1/user/authenticate") {
                setBody(credentialsJson)
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertEquals(response.content, "Username not found")
            }
        }
    }

    @Test
    fun testAuthenticationFailedPassword() {

        val credentialsJson = Json.encodeToString(
            AuthCredentials.serializer(), AuthCredentials("MarkusT", "NotThePassword")
        )

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            handleRequest(HttpMethod.Post, "/api/v1/user/authenticate") {
                setBody(credentialsJson)
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertEquals(response.content, "Password not correct")
            }
        }
    }
}
