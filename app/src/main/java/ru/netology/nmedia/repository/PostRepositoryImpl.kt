package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostEntity

class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .build()
//    private val gson = Gson()
//    private val typeToken = object : TypeToken<List<Post>>() {}
//
//    companion object {
//        private val jsonType = "application/json".toMediaType()
//    }


    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAll() {
        val response = PostsApi.retrofitService.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException(response.code().toString())
        }
        val posts = response.body() ?: throw RuntimeException("body is null")

        postDao.insert(posts.map(PostEntity.Companion::fromDto))
    }

    override suspend fun save(post: Post) {
        PostsApi.retrofitService.save(post)
//            .enqueue(object : Callback<Post> {
//                override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                    try {
//                        if (!response.isSuccessful) {
//                            callback.onError(response.code().toString())
//                            return
//                        }
//                        callback.onSuccess(
//                            response.body() ?: throw RuntimeException("body is null")
//                        )
//                    } catch (e: Exception) {
//                        callback.onError(e.toString())
//                    }
//                }
//
//                override fun onFailure(call: Call<Post>, t: Throwable) {
//                    callback.onError(java.lang.Exception(t).toString())
//                }
//            })
    }

    override suspend fun likeById(id: Long, willLike: Boolean): Post {
        val request = if (willLike)
            PostsApi.retrofitService.likeById(id)
        else
            PostsApi.retrofitService.dislikeById(id)
        return Post(123,"32","213","3213","3213")
        //TODO
//        request.enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                try {
//                    if (!response.isSuccessful) {
//                        callback.onError(response.code().toString())
//                        return
//                    }
//                    callback.onSuccess(
//                        response.body() ?: throw RuntimeException("body is null")
//                    )
//                } catch (e: Exception) {
//                    callback.onError(e.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                callback.onError(java.lang.Exception(t).toString())
//            }
//        })
    }

    override suspend fun shareById(id: Long) {
        //TODO
    }

    override suspend fun removeById(id: Long) {
        PostsApi.retrofitService.removeById(id)
//            .enqueue(object : Callback<Unit> {
//                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
//                    if (!response.isSuccessful) {
//                        callback.onError(response.code().toString())
//                        return
//                    }
//                    callback.onSuccess()
//                }
//
//                override fun onFailure(call: Call<Unit>, t: Throwable) {
//                    callback.onError(java.lang.Exception(t).toString())
//                }
//
//            })
    }
}