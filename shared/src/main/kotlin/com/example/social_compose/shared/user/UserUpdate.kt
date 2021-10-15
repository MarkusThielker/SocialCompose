package com.example.social_compose.shared.user

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdate(
    val username: String,
    val alias: String,
    val description: String,
    val image: String,
    val color: String,
)
