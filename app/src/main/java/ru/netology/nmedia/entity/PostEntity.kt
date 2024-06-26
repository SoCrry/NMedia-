package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shared: Int = 0,
    val views: Int = 0,
    val visibilityCount: Int = 0,
    val urlVideo: String? = null,
    val authorAvatar: String? = null,
) {
    fun toDto() = Post(id, author, content, published, likedByMe, likes, shared, views, visibilityCount, urlVideo, authorAvatar )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likedByMe, dto.likes,dto.shared,dto.views,dto.visibilityCount,dto.urlVideo,dto.authorAvatar)

    }
}