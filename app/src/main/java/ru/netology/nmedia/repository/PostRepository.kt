package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.FeedItem
import ru.netology.nmedia.Post
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun showNewPosts()
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun likeById(id: Long, willLike: Boolean): Post
    suspend fun saveWithAttachment(post: Post, file: File)
}