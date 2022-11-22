package ru.netology.nmedia.repository

import ru.netology.nmedia.Post

interface PostRepository {
    fun getAll():List<Post>
    fun save (post: Post) : Post
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
}