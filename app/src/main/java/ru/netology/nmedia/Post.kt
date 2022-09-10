package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likesAmount: Long = 0,
    var sharesAmount: Long = 0,
    var likedByMe: Boolean = false,
    var views: Long=0
)
