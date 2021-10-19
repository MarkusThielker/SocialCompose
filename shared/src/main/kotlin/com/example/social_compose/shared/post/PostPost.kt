package com.example.social_compose.shared.post

import kotlinx.serialization.Serializable

@Serializable
data class PostPost(
    val parentId: Long?,
    val userId: Long,
    val content: String,
)
