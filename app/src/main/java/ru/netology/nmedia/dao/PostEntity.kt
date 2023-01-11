package ru.netology.nmedia.dao

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.Attachment
import ru.netology.nmedia.AttachmentType
import ru.netology.nmedia.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likesAmount: Long = 0,
    val sharesAmount: Long = 0,
    val likedByMe: Boolean = false,
    val views: Long = 0,
    @Embedded
    val attachment: AttachmentEmbedabble?,
    val notOnServer: Boolean = false,
    val show: Boolean = true,
) {
    fun toDto() = Post(
        id,
        author,
        authorAvatar,
        content,
        published,
        likesAmount,
        sharesAmount,
        likedByMe,
        views,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post, notOnServer: Boolean = false) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likes,
                dto.sharesAmount,
                dto.likedByMe,
                dto.views,
                AttachmentEmbedabble.fromDto(dto.attachment)
            )
    }
}

data class AttachmentEmbedabble(
    val url: String,
    val description: String,
    val type: AttachmentType
) {
    fun toDto() = Attachment(url, description, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbedabble(it.url, it.description, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(show: Boolean = true): List<PostEntity> = map(PostEntity::fromDto)
    .map { it.copy(show = show) }