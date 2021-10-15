package com.example.social_compose.shared.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistration(
    val username: String,
    val email: String,
    val password: String,
    val alias: String,
)
