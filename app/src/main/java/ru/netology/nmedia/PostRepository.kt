package ru.netology.nmedia

import androidx.lifecycle.LiveData

interface PostRepository {
    fun getAll():LiveData<List<Post>>
    fun save (post: Post)
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
}