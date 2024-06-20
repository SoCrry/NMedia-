package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var like: Int,
    val shared: Int,
    val views: Int,
    val visibilityCount: Int = 0,
    val urlVideo: String? = null
)
