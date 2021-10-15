package com.example.social_compose.shared.user

import kotlinx.serialization.Serializable

@Serializable
class UserPrivate(
    val userId: Long,
    val username: String,
    val email: String,
    val password: String,
    val alias: String,
    val description: String,
    val image: String,
    val color: String,
    val verified: Boolean,
    val email_confirmed: Boolean,
    val created_at: String,
) {

    fun toPublicUser(): UserPublic =
        UserPublic(
            userId,
            username,
            alias,
            description,
            image,
            color,
            verified,
            created_at,
        )
}
