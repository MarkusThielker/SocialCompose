package com.example.social_compose.shared.post

import com.example.social_compose.shared.user.UserPublic
import kotlinx.serialization.Serializable

@Serializable
data class PostGet(
    val postId: Long,
    val parentId: PostGet?,
    val user: UserPublic?,
    val content: String,
    val created_at: String,
)
