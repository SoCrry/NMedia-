package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likeByMe: Boolean = false,
    var like: Int,
    var shared: Int,
    var views: Int
)
