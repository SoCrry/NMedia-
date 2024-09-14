package ru.netology.nmedia.repository


import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

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
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media

}