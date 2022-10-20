package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save (post: Post) : Post
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
}