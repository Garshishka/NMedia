package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.Attachment
import ru.netology.nmedia.AttachmentType
import ru.netology.nmedia.Media
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(apiService, postDao, postRemoteKeyDao, appDb),
    ).flow
        .map { it.map(PostEntity::toDto) }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        delay(10_000)
        while (true) {
            val response = apiService.getNewer(id)
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
        val response = apiService.getAll()
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
            val response = apiService.save(post)
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

            val response = apiService.upload(data)
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
                apiService.likeById(id)
            else
                apiService.dislikeById(id)
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
            val response = apiService.removeById(id)
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