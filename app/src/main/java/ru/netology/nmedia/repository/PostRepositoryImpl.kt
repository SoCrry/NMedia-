package ru.netology.nmedia.repository




import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.AppUnknownError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.PhotoModel
import kotlin.coroutines.cancellation.CancellationException


import kotlin.time.Duration.Companion.seconds

class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
    override val data: Flow<List<Post>> = postDao.getAll().map{
        it.map(PostEntity::toDto)
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        val media = try {
            upload(upload)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw AppUnknownError
        }
        val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
        save(postWithAttachment)
    }
    override suspend fun upload(upload: MediaUpload): Media {
        val file = upload.file ?: throw IllegalArgumentException("Photo file is missing")
        val response = PostsApi.retrofitService.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }


    override fun getNewerCount(newerId: Long): Flow<Int> = flow  {
while (true){
    delay(10.seconds)
    try {
        val  postsResponse = PostsApi.retrofitService.getNewer(newerId)
        val posts = postsResponse.body().orEmpty()
        emit(posts.size)
        postDao.insert(posts.toEntity(hidden = true))
    }catch (e: CancellationException){
throw e
    }catch (e: Exception){
        //ignore
    }
}
    }
    override suspend fun viewNewPost() {
        postDao.viewNewPost()
    }

    override suspend fun sharedById(id: Long) {
        //TODO:
    }

    override suspend fun getAvatarUrl(fileName: String): String {
        return "${BASE_URL}/avatars/$fileName"
    }

    override suspend fun likeById(id: Long): Post {
        postDao.likeById(id)
        try {
            val response = PostsApi.retrofitService.likeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            val post = response.body() ?: throw RuntimeException("body is null")
            postDao.insert(PostEntity.fromDto(post))
            return post
        } catch (e: IOException) {
            postDao.likeById(id)
            throw NetworkError
        } catch (e: Exception) {
            postDao.likeById(id)
            throw AppUnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            postDao.insert(
                PostEntity.fromDto(
                    response.body() ?: throw RuntimeException("body is null")
                )
            )

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw AppUnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        postDao.removeById(id)
        val response = PostsApi.retrofitService.removeById(id)
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }
    }

    override suspend fun unLikeById(id: Long): Post {
        postDao.likeById(id)
        try {
            val response = PostsApi.retrofitService.dislikeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            val post = response.body() ?: throw RuntimeException("body is null")
            postDao.insert(PostEntity.fromDto(post))
            return post
        } catch (e: IOException) {
            postDao.likeById(id)
            throw NetworkError
        } catch (e: Exception) {
            postDao.likeById(id)
            throw AppUnknownError
        }
    }

    override suspend fun getAllAsync() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val posts = response.body() ?: throw RuntimeException("Response body is empty")
            postDao.insert(posts.map(PostEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw AppUnknownError
        }
    }
    /* private val client = OkHttpClient.Builder()
         .connectTimeout(30, TimeUnit.SECONDS)
         .build()
     private val gson = Gson()
     private val typeToken = object : TypeToken<List<Post>>() {}

     companion object {
         private const val BASE_URL = "http://10.0.2.2:9999"
         private val jsonType = "application/json".toMediaType()
     }*/

    /* override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }*//*

    override fun likeById(id: Long, callback: PostRepository.NMediaCallback<Post>) {
        PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                } else {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun sharedById(id: Long) {
        //TODO:
    }

    *//*override fun getAllAsync(callback: PostRepository.NMediaCallback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(
                call: Call<List<Post>>, response: Response<List<Post>>
            ) { if (!response.isSuccessful) {
                callback.onError(RuntimeException(response.message()))
            } else {
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }*//*

    override fun getAvatarUrl(fileName: String): String {
        return "${BASE_URL}/avatars/$fileName"
    }

    override fun save(post: Post, callback: PostRepository.NMediaCallback<Post>) {
       PostsApi.retrofitService.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }


    override fun removeById(id: Long, callback: PostRepository.NMediaCallback<Unit>) {
        PostsApi.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }else {
                        callback.onSuccess(Unit)
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun unLikeById(id: Long, callback: PostRepository.NMediaCallback<Post>) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                } else {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }*/

}