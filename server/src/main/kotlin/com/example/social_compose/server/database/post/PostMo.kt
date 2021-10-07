package com.example.social_compose.server.database.post

import com.example.social_compose.server.ServerConfig.postTableName
import org.jetbrains.exposed.sql.Table

object PostMo : Table(name = postTableName) {
    val postId = long("postId").autoIncrement().primaryKey()
    val parentId = long("parentId").nullable()
    val userId = long("userId")
    val content = varchar("content", 280)
    val timestamp = datetime("timestamp")
}
