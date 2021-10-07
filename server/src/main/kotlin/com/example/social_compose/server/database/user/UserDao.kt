package com.example.social_compose.server.database.user

import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class UserDao(db: Database) : Closeable {

    init {
        transaction(db) {
            SchemaUtils.create(UserMo)
        }
    }

    override fun close() {}
}
