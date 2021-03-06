package com.example.social_compose.server.database.user

import com.example.social_compose.shared.user.UserPrivate
import com.example.social_compose.shared.user.UserRegistration
import com.example.social_compose.shared.user.UserUpdate
import io.ktor.utils.io.core.Closeable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt

/**
 * The user's data access object (DAO) offers methods to access the user table.
 * The creation of the DAO triggers a table creation, if no table following the [UserMo] schema exists.
 *
 * @param db the database to read and write user's from/to
 *
 * @author Markus Thielker
 *
 * */
class UserDao(private val db: Database) : Closeable {

    init {
        transaction(db) {
            SchemaUtils.create(UserMo)
        }
    }

    fun createUser(user: UserRegistration) = transaction(db) {

        UserMo.insert {
            it[username] = user.username
            it[email] = user.email.lowercase()
            it[password] = BCrypt.hashpw(user.password, BCrypt.gensalt())
            it[alias] = user.alias
        }
        Unit
    }

    fun updateUser(user: UserUpdate) = transaction {

        UserMo.update {
            it[username] = user.username
            it[alias] = user.alias
            it[description] = user.description
            it[image] = user.image
            it[color] = user.color
        }
        Unit
    }

    fun removeUser(userId: Long) = transaction(db) {

        UserMo.deleteWhere { UserMo.userId eq userId }
        Unit
    }

    fun getUserById(userId: Long): UserPrivate? = transaction(db) {

        val singleOrNull = UserMo
            .select { UserMo.userId eq userId }
            .map {
                UserPrivate(
                    it[UserMo.userId],
                    it[UserMo.username],
                    it[UserMo.email],
                    it[UserMo.password],
                    it[UserMo.alias],
                    it[UserMo.description],
                    it[UserMo.image],
                    it[UserMo.color],
                    it[UserMo.verified],
                    it[UserMo.email_confirmed],
                    it[UserMo.created_at].toString()
                )
            }
            .singleOrNull()
        singleOrNull
    }

    fun getUserByUsername(username: String): UserPrivate? = transaction(db) {

        val singleOrNull = UserMo
            .select { UserMo.username.lowerCase() eq username.lowercase() }
            .map {
                UserPrivate(
                    it[UserMo.userId],
                    it[UserMo.username],
                    it[UserMo.email],
                    it[UserMo.password],
                    it[UserMo.alias],
                    it[UserMo.description],
                    it[UserMo.image],
                    it[UserMo.color],
                    it[UserMo.verified],
                    it[UserMo.email_confirmed],
                    it[UserMo.created_at].toString()
                )
            }
            .singleOrNull()
        singleOrNull
    }

    fun getIdByUsername(username: String): Long? = transaction(db) {

        val singleOrNull = UserMo
            .select { UserMo.username.lowerCase() eq username.lowercase() }
            .map { it[UserMo.userId] }
            .singleOrNull()
        singleOrNull
    }

    fun usernameTaken(username: String): Boolean = transaction(db) {

        val singleOrNull = UserMo.select { UserMo.username.lowerCase() eq username.lowercase() }.singleOrNull()
        singleOrNull != null
    }

    fun emailTaken(email: String): Boolean = transaction(db) {

        val singleOrNull = UserMo.select { UserMo.email.lowerCase() eq email.lowercase() }.singleOrNull()
        singleOrNull != null
    }

    fun getUserCount(): Int = transaction(db) {

        val userCount = UserMo.select { UserMo.userId neq -1 }.count()
        userCount
    }

    override fun close() {}
}
