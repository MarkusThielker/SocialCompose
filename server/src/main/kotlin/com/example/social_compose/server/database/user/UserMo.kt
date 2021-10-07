package com.example.social_compose.server.database.user

import com.example.social_compose.server.ServerConfig.userTableName
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import org.joda.time.Instant

object UserMo : Table(name = userTableName) {
    val userId = long("userId").autoIncrement().uniqueIndex("UX_USER_TABLE_userId").primaryKey()
    val username = varchar("username", 16).uniqueIndex("UX_USER_TABLE_username")
    val email = varchar("email", 100).uniqueIndex("UX_USER_TABLE_email")
    val password = varchar("password", 256)
    val alias = varchar("alias", 50)
    val description = varchar("description", 160).default("")
    val image = varchar("image", 256).default("https://server.markus-thielker.de/img/profile.img.jpg")
    val color = varchar("color", 6).default("7700FF")
    val verified = bool("verified").default(false)
    val email_confirmed = bool("email_confirmed").default(false)
    val created_at = datetime("created_at").default(DateTime(Instant.now()))
}
