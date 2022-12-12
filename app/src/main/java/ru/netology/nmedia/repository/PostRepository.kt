package ru.netology.nmedia.repository

import ru.netology.nmedia.Post

interface PostRepository {

    fun getAll(callback: GetAllCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    fun save(post: Post, callback: SaveCallback)
    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    fun likeById(id: Long, willLike: Boolean, callback: LikeCallback)
    interface LikeCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    fun shareById(id: Long)

    fun removeById(id: Long, callback: RemoveCallback)
    interface RemoveCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }
}