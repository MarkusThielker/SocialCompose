package com.example.social_compose.server.database.post

import com.example.social_compose.server.ServerConfig.postTableName
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import org.joda.time.Instant

/**
 * The schema for the database's post table.
 * Create a [PostDao] instance for your database to set up the table.
 *
 * @author Markus Thielker
 *
 * */
object PostMo : Table(name = postTableName) {
    val postId = long("postId").autoIncrement().primaryKey()
    val parentId = long("parentId").nullable()
    val userId = long("userId")
    val content = varchar("content", 280)
    val created_at = datetime("created_at").default(DateTime(Instant.now()))
}
