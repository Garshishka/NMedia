package ru.netology.nmedia.repository

import ru.netology.nmedia.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun getAllAsync(callback: GetAllCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    fun save(post: Post): Post
    fun likeById(id: Long): Post
    fun unLikeById(id: Long): Post
    fun shareById(id: Long)
    fun removeById(id: Long)
}