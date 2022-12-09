package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostsApi
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl() : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: PostRepository.GetAllCallback) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    try {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(java.lang.Exception(t))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.SaveCallback) {
        PostsApi.retrofitService.save(post)
            .enqueue()

        val request: Request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/api/slow/posts")
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
                .url("${BuildConfig.BASE_URL}/api/slow/posts/$id/likes")
                .build()
        } else {
            Request.Builder()
                .delete(gson.toJson(id).toRequestBody(jsonType))
                .url("${BuildConfig.BASE_URL}/api/slow/posts/$id/likes")
                .build()
        }
    }

    override fun likeById(id: Long, willLike: Boolean, callback: PostRepository.LikeCallback) {
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

    override fun removeById(id: Long, callback: PostRepository.RemoveCallback) {
        val request: Request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/api/slow/posts/$id")
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