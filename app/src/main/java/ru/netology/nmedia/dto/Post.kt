package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val authorId: Long,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Int,
    val shared: Int,
    val views: Int,
    val visibilityCount: Int = 0,
    val urlVideo: String? = null,
    val authorAvatar: String? = null,
    val hidden: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,


    )


data class Attachment(
    val url: String,
    val type: AttachmentType,
)
