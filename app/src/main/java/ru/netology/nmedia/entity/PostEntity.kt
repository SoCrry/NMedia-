package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shared: Int = 0,
    val views: Int = 0,
    val visibilityCount: Int = 0,
    val urlVideo: String? = null,
    val authorAvatar: String? = null,
    val hidden: Boolean = false,
    @Embedded
    var attachment: AttachmentEmbeddable?,

    ) {
    fun toDto() = Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        shared = shared,
        views = views,
        visibilityCount = visibilityCount,
        urlVideo = urlVideo,
        authorAvatar = authorAvatar,
        hidden = hidden,
        attachment = attachment?.toDto(),
        authorId = authorId,

        )

    companion object {
        fun fromDto(dto: Post, hidden: Boolean = false) =
            PostEntity(
                id = dto.id,
                author = dto.author,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                likes = dto.likes,
                shared = dto.shared,
                views = dto.views,
                visibilityCount = dto.visibilityCount,
                urlVideo = dto.urlVideo,
                authorAvatar = dto.authorAvatar,
                hidden = hidden,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment),
                authorId = dto.authorId

            )

    }
}

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(hidden: Boolean = false): List<PostEntity> =
    map { PostEntity.fromDto(it, hidden) }
