package com.example.social_compose.server

import com.example.social_compose.server.plugins.configureSecurity
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class ApplicationTest {

    @Test
    fun testRoot() {
        withTestApplication({ configureSecurity() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(Json.encodeToString(mapOf("message" to "Hello, world!")), response.content)
            }
        }
    }
}
