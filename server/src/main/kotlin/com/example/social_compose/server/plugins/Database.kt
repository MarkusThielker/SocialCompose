package com.example.social_compose.server.plugins

import com.example.social_compose.server.ServerConfig
import com.example.social_compose.server.database.post.PostDao
import com.example.social_compose.server.database.user.UserDao
import io.ktor.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Set up the database by creating data access objects (DAOs).
 * The DAO instances are stored in the server configuration object to be accessible from everywhere.
 *
 * @author Markus Thielker
 *
 * */
fun Application.configureDatabase() {

    CoroutineScope(Dispatchers.IO).launch {

        // 5s delay to wait for mariadb container to start up completely
        delay(5000)

        ServerConfig.postDao = PostDao(ServerConfig.mariaDB)
        ServerConfig.userDao = UserDao(ServerConfig.mariaDB)

        log.info("Connection to database established.")
    }
}
