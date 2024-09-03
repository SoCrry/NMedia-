package ru.netology.nmedia.repository


import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewerCount(newerId: Long): Flow<Int>
    suspend fun likeById(id: Long): Post
    suspend fun sharedById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun unLikeById(id: Long): Post
    suspend fun getAllAsync()
    suspend fun getAvatarUrl(fileName: String): String
    suspend fun viewNewPost()

}