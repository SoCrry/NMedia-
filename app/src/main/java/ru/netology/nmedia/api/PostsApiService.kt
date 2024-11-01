package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
import java.util.concurrent.TimeUnit

private const val BASE_URL = "${BuildConfig.BASE_URL}api/"


private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
        chain.proceed(
            AppAuth.getInstance().data.value?.token?.let {
                chain.request().newBuilder()
                    .addHeader("Authorization", it)
                    .build()
            } ?: chain.request()
        )
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authenticate(
        @Field("login") login: String,
        @Field("pass") password: String
    ): Response<Token>

    @POST("users/push-token")
    suspend fun sendPushToken(@Body token: PushToken)
}

object PostsApi {
    val retrofitService: PostsApiService by lazy {
        retrofit.create<PostsApiService>()
    }
}