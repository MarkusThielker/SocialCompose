package com.example.social_compose.server

import com.example.social_compose.server.plugins.configureDatabase
import com.example.social_compose.server.plugins.configureSecurity
import com.example.social_compose.shared.user.UserRegistration
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class RegistrationTest {

    @Test
    fun testRegistrationFailedDuplicateUsername() {

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            val userRegistration = UserRegistration(
                "MarkusT",
                "null@null.com",
                "password",
                "alias",
            )

            handleRequest(HttpMethod.Post, "/api/v1/user/create") {
                setBody(Json.encodeToString(userRegistration))
            }.apply {
                assertEquals(HttpStatusCode.Conflict, response.status())
                assertEquals("Username is already used by another account", response.content)
            }
        }
    }

    @Test
    fun testRegistrationFailedDuplicateEmail() {

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            val userRegistration = UserRegistration(
                "username",
                "max.mustermann@example.com",
                "password",
                "alias",
            )

            handleRequest(HttpMethod.Post, "/api/v1/user/create") {
                setBody(Json.encodeToString(userRegistration))
            }.apply {
                assertEquals(HttpStatusCode.Conflict, response.status())
                assertEquals("Email is already used by another account", response.content)
            }
        }
    }

    @Test
    fun testRegistrationFailedFormatUsername() {

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            val userRegistration = UserRegistration(
                "usernameIsTooLong",
                "null@null.com",
                "password",
                "alias",
            )

            handleRequest(HttpMethod.Post, "/api/v1/user/create") {
                setBody(Json.encodeToString(userRegistration))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "The data sent for registration doesn't match the format requirements", response.content
                )
            }
        }
    }

    @Test
    fun testRegistrationFailedFormatEmail() {

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            val userRegistration = UserRegistration(
                "username",
                "invalidEmail",
                "password",
                "alias",
            )

            handleRequest(HttpMethod.Post, "/api/v1/user/create") {
                setBody(Json.encodeToString(userRegistration))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "The data sent for registration doesn't match the format requirements", response.content
                )
            }
        }
    }

    @Test
    fun testRegistrationFailedFormatPassword() {

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            val userRegistration = UserRegistration(
                "username",
                "null@null.com",
                "short",
                "alias",
            )

            handleRequest(HttpMethod.Post, "/api/v1/user/create") {
                setBody(Json.encodeToString(userRegistration))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "The data sent for registration doesn't match the format requirements", response.content
                )
            }
        }
    }

    @Test
    fun testRegistrationFailedFormatAlias() {

        withTestApplication({ configureDatabase(); configureSecurity() }) {

            val userRegistration = UserRegistration(
                "username",
                "null@null.com",
                "password",
                "", // missing alias
            )

            handleRequest(HttpMethod.Post, "/api/v1/user/create") {
                setBody(Json.encodeToString(userRegistration))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "The data sent for registration doesn't match the format requirements", response.content
                )
            }
        }
    }
}