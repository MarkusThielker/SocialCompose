package com.example.social_compose.server.database.post

import com.example.social_compose.server.ServerConfig.postDao
import com.example.social_compose.server.ServerConfig.userDao
import com.example.social_compose.shared.post.PostGet
import com.example.social_compose.shared.post.PostPost
import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * The post's data access object (DAO) offers methods to access the post table.
 * The creation of the DAO triggers a table creation, if no table following the [PostMo] schema exists.
 *
 * @param db the database to read and write post's from/to
 *
 * @author Markus Thielker
 *
 * */
class PostDao(private val db: Database) : Closeable {

    init {
        transaction(db) {
            SchemaUtils.create(PostMo)
        }
    }

    /**
     * Inserts a [post][PostPost] into the database.
     *
     * @author Markus Thielker
     *
     * */
    fun createPost(post: PostPost) = transaction(db) {

        PostMo.insert {
            it[parentId] = post.parentId
            it[userId] = post.userId
            it[content] = post.content
        }
    }

    /**
     * Removes a post from the database.
     *
     * @param postId The id of the post that's supposed to be deleted.
     *
     * @author Markus Thielker
     *
     * */
    fun removePost(postId: Long) = transaction(db) {

        PostMo.deleteWhere { PostMo.postId eq postId }
        Unit
    }

    /**
     * Queries a post by searching for the postId.
     * Returns null if the postId wasn't found.
     * The [post][PostGet] contains the full [user object][com.example.social_compose.shared.user.UserPublic]
     * and all the parent posts.
     *
     * @return The [post][PostGet] object that was queried or null
     *
     * @author Markus Thielker
     *
     * */
    fun getPostById(postId: Long): PostGet? = transaction(db) {

        val singleOrNull = PostMo
            .select { PostMo.postId eq postId }
            .map {

                val user = userDao.getUserById(it[PostMo.userId])?.toPublicUser()
                val parent = if (it[PostMo.parentId] != null) postDao.getPostById(it[PostMo.parentId]!!) else null

                PostGet(
                    it[PostMo.postId],
                    parent,
                    user,
                    it[PostMo.content],
                    it[PostMo.created_at].toString(),
                )
            }
            .singleOrNull()
        singleOrNull
    }

    /**
     * Queries 20 posts, starting from the passed index.
     * The Query searches all posts in the database, ordered by the time they were posted.
     *
     * @param username Filter for the account the posts are from.
     * If the username does not exist, an empty list is returned
     *
     * @return A list of [post][PostGet] objects that were queried
     *
     * @author Markus Thielker
     *
     * */
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
                val parent = if (it[PostMo.parentId] != null) postDao.getPostById(it[PostMo.parentId]!!) else null

                PostGet(
                    it[PostMo.postId],
                    parent,
                    user,
                    it[PostMo.content],
                    it[PostMo.created_at].toString(),
                )
            }
        list
    }

    /**
     * Searches the post with the passed postId.
     *
     * @return A posts with the postId was found.
     *
     * @author Markus Thielker
     *
     * */
    fun isValidPostId(postId: Long): Boolean {
        return getPostById(postId) != null
    }

    override fun close() {}
}
