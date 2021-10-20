package com.example.social_compose.server.database.post

import com.example.social_compose.server.ServerConfig.userDao
import com.example.social_compose.shared.post.PostGet
import com.example.social_compose.shared.post.PostPost
import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PostDao(private val db: Database) : Closeable {

    init {
        transaction(db) {
            SchemaUtils.create(PostMo)
        }
    }

    fun createPost(post: PostPost) = transaction(db) {

        PostMo.insert {
            it[parentId] = post.parentId
            it[userId] = post.userId
            it[content] = post.content
        }
    }

    fun removePost(postId: Long) = transaction(db) {

        PostMo.deleteWhere { PostMo.postId eq postId }
        Unit
    }

    fun getPostById(postId: Long): PostGet? = transaction(db) {

        val singleOrNull = PostMo
            .select { PostMo.postId eq postId }
            .map {

                val user = userDao.getUserById(it[PostMo.userId])?.toPublicUser()

                PostGet(
                    it[PostMo.postId],
                    it[PostMo.parentId],
                    user,
                    it[PostMo.content],
                    it[PostMo.created_at].toString(),
                )
            }
            .singleOrNull()
        singleOrNull
    }

    fun getPostsByIndex(index: Int = 0, username: String = ""): List<PostGet> = transaction(db) {

        val usernamePassed = username.isNotEmpty()
        var userId: Long = -1L

        if (usernamePassed) {
            userId = userDao.getIdByUsername(username) ?: return@transaction listOf()
        }

        val list = PostMo
            .select { if (usernamePassed) PostMo.userId eq userId else PostMo.userId greaterEq 0 }
            .orderBy(PostMo.created_at, SortOrder.DESC)
            .limit(20, index)
            .map {

                val user = userDao.getUserByUsername(username)?.toPublicUser()

                PostGet(
                    it[PostMo.postId],
                    it[PostMo.parentId],
                    user,
                    it[PostMo.content],
                    it[PostMo.created_at].toString(),
                )
            }
        list
    }

    fun isValidPostId(postId: Long): Boolean {
        return getPostById(postId) != null
    }

    override fun close() {}
}
