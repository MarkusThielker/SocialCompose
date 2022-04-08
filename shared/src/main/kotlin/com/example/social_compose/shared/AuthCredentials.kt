package com.example.social_compose.shared

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredentials(
    val username: String,
    val password: String,
    val requestPermanent: Boolean = false,
)
