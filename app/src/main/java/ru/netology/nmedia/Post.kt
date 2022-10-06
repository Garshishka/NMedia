package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likesAmount: Long = 0,
    val sharesAmount: Long = 0,
    val likedByMe: Boolean = false,
    val views: Long = 0,
    val attachedVideo: String = ""
)
