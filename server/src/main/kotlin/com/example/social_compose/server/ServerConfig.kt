package com.example.social_compose.server

import com.example.social_compose.server.database.post.PostDao
import com.example.social_compose.server.database.user.UserDao
import org.jetbrains.exposed.sql.Database

object ServerConfig {

    // server setup
    const val host = "localhost"
    const val port = 8080

    // authentication
    const val issuer = "https://server.markus-thielker.de"
    const val secret = "secret"
    const val validity = 900000 // ms -> 15 minutes

    // database
    const val postTableName = "POST_TABLE"
    const val userTableName = "USER_TABLE"

    lateinit var postDao: PostDao
    lateinit var userDao: UserDao

    val mariaDB = Database.connect(
        url = "jdbc:mariadb://server.markus-thielker.de:3306/social_compose",
        driver = "org.mariadb.jdbc.Driver",
        user = "***",
        password = "***"
    )
}
