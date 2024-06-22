package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long):Post
    fun sharedById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
    fun unLikeById(id: Long):Post

}