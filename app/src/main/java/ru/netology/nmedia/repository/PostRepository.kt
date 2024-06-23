package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun likeById(id: Long,callback: NMediaCallback<Post>)
    fun sharedById(id: Long)
    fun save(post: Post,callback: NMediaCallback<Post>)
    fun removeById(id: Long,callback: NMediaCallback<Any>)
    fun unLikeById(id: Long,callback: NMediaCallback<Post>)

    fun getAllAsync(callback: NMediaCallback<List<Post>>)

    interface NMediaCallback<T> {
        fun onSuccess(data: T) {}
        fun onError(e: Exception) {}
    }
}