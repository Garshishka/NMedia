package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl() : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            } ?: emptyList()
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun save(post: Post): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        return client.newCall(request).execute()
            .let {
                it.body?.string()
            }?.let {
                gson.fromJson(it, Post::class.java)
            } ?: error("Empty response body")
    }

    override fun saveAsync(post: Post, callback: PostRepository.SaveCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(e)
                }
            }
        })
    }

    private fun getLikeReq(id: Long, willLike: Boolean): Request {
        return if (willLike) {
            Request.Builder()
                .post(gson.toJson(id).toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()
        } else {
            Request.Builder()
                .delete(gson.toJson(id).toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()
        }
    }

    override fun likeById(id: Long, willLike: Boolean): Post {
        val request = getLikeReq(id, willLike)

        return client.newCall(request)
            .execute().let {
                it.body?.string()
            }?.let {
                gson.fromJson(it, Post::class.java)
            } ?: error("Empty response body")
    }

    override fun likeByIdAsync(id: Long, willLike: Boolean, callback: PostRepository.LikeCallback) {
        val request = getLikeReq(id, willLike)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(e)
                }
            }

        })
    }

    override fun shareById(id: Long) {
        //TODO
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .delete()
            .build()

        client.newCall(request).execute()
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemoveCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess()
            }
        })
    }
}