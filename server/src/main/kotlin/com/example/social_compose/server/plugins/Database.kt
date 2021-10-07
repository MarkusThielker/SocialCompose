package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig
import com.example.social_compose.server.database.post.PostDao
import com.example.social_compose.server.database.user.UserDao
import io.ktor.application.*

fun Application.configureDatabase() {

    ServerConfig.postDao = PostDao(ServerConfig.mariaDB)
    ServerConfig.userDao = UserDao(ServerConfig.mariaDB)
}
