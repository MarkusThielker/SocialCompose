package com.example.social_compose.server

import com.example.social_compose.server.database.post.PostDao
import com.example.social_compose.server.database.user.UserDao
import org.jetbrains.exposed.sql.Database

object ServerConfig {

    // server setup
    const val host = "0.0.0.0"
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
        url = "jdbc:mariadb://mariadb:3306/social_compose",
        driver = "org.mariadb.jdbc.Driver",
        user = "root",
        password = "password",
    )
}
