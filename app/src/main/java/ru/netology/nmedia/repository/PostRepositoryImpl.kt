package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.Attachment
import ru.netology.nmedia.AttachmentType
import ru.netology.nmedia.Media
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostEntity
import ru.netology.nmedia.dao.toDto
import ru.netology.nmedia.dao.toEntity
import java.io.File

class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {

    override val data = postDao.getAll().map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        delay(10_000)
        while (true) {
            val response = PostsApi.retrofitService.getNewer(id)
            val newPosts = response.body() ?: error("Empty response")
            postDao.insert(newPosts.toEntity(false))
            emit(newPosts.size)
            delay(10_000)
        }
    }

    override suspend fun showNewPosts() {
        postDao.showUnseen()
    }

    override suspend fun getAll() {
        val response = PostsApi.retrofitService.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException(response.code().toString())
        }
        val posts = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(posts.map(PostEntity.Companion::fromDto))

        postDao.getAllUnsent().forEach { save(it.toDto()) }
    }

    override suspend fun save(post: Post) {
        postDao.save(PostEntity.fromDto(post, true))
        try {
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw  RuntimeException(
                    response.code().toString()
                )
            }
            val body = response.body() ?: throw RuntimeException("body is null")
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private suspend fun upload(file: File): Media {
        try {
            val data = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())

            val response = PostsApi.retrofitService.upload(data)
            if (!response.isSuccessful) {
                throw  RuntimeException(
                    response.code().toString()
                )
            }
            return response.body() ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override suspend fun saveWithAttachment(post: Post, file: File) {
        try {
            val upload = upload(file)
            val postWithAttachment =
                post.copy(attachment = Attachment(upload.id, "photo", AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override suspend fun likeById(id: Long, willLike: Boolean): Post {
        postDao.likeById(id)
        try {
            val response = if (willLike)
                PostsApi.retrofitService.likeById(id)
            else
                PostsApi.retrofitService.dislikeById(id)
            if (!response.isSuccessful) {
                postDao.likeById(id)
                throw RuntimeException(response.code().toString())
            }
            return response.body() ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            postDao.likeById(id)
            throw RuntimeException(e)
        }
    }


    override suspend fun shareById(id: Long) {
        //TODO
    }

    override suspend fun removeById(id: Long) {
        val removed = postDao.getById(id)
        postDao.removeById(id)
        try {
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                postDao.insert(removed)
                throw RuntimeException(response.code().toString())
            }
        } catch (e: Exception) {
            postDao.insert(removed)
            throw  RuntimeException(e)
        }
    }
}