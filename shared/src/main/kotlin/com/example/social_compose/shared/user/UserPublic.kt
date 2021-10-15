package com.example.social_compose.shared.user

import kotlinx.serialization.Serializable

@Serializable
data class UserPublic(
    val userId: Long,
    val username: String,
    val alias: String,
    val description: String,
    val image: String,
    val color: String,
    val verified: Boolean,
    val created_at: String,
)
