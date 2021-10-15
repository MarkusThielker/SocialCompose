package com.example.social_compose.shared

import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
    val token: String,
)
