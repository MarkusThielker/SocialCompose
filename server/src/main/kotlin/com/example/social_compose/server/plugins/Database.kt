package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig
import com.example.social_compose.server.database.post.PostDao
import com.example.social_compose.server.database.user.UserDao
import io.ktor.application.*

/**
 * Set up the database by creating data access objects (DAOs).
 * The DAO instances are stored in the server configuration object to be accessible from everywhere.
 *
 * @author Markus Thielker
 *
 * */
fun Application.configureDatabase() {

    ServerConfig.postDao = PostDao(ServerConfig.mariaDB)
    ServerConfig.userDao = UserDao(ServerConfig.mariaDB)
}
