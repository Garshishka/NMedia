package ru.netology.nmedia

import android.net.Uri
import java.io.File

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
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val authorId: Long = 0,
)

data class PhotoModel(val uri: Uri?, val file: File?)
data class Media(val id: String)

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType = AttachmentType.IMAGE
)

enum class AttachmentType {
    IMAGE
}
