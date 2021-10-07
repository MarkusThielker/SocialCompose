package com.example.social_compose.server.database.post

import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class PostDao(db: Database) : Closeable {

    init {
        transaction(db) {
            SchemaUtils.create(PostMo)
        }
    }

    override fun close() {}
}
