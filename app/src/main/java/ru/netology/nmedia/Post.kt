package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Long = 0,
    val sharesAmount: Long = 0,
    val likedByMe: Boolean = false,
    val views: Long = 0,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType = AttachmentType.IMAGE
)

enum class AttachmentType {
    IMAGE
}
