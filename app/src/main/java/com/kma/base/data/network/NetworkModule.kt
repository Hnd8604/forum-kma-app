package com.kma.base.data.network

import android.content.Context
import com.kma.base.data.api.*
import com.kma.base.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    // BASE_URL từ Backend - thay đổi nếu cần
    private const val BASE_URL = "http://72.60.198.235:8080/api/v1/"
    
    private lateinit var tokenManager: TokenManager
    
    fun initialize(context: Context) {
        tokenManager = TokenManager(context)
    }
    
    fun getTokenManager(): TokenManager {
        if (!::tokenManager.isInitialized) {
            throw IllegalStateException("NetworkModule not initialized. Call initialize(context) first.")
        }
        return tokenManager
    }
    
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        
        // Add authorization header if token exists
        if (::tokenManager.isInitialized) {
            runBlocking {
                val token = tokenManager.getAccessTokenSync()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
            }
        }
        
        chain.proceed(requestBuilder.build())
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
    
    val postApi: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
    
    val interactionApi: InteractionApiService by lazy {
        retrofit.create(InteractionApiService::class.java)
    }
    
    val commentApi: CommentApiService by lazy {
        retrofit.create(CommentApiService::class.java)
    }
    
    val chatApi: ChatApiService by lazy {
        retrofit.create(ChatApiService::class.java)
    }
    
    val groupApi: GroupApiService by lazy {
        retrofit.create(GroupApiService::class.java)
    }
    
    val friendApi: FriendApiService by lazy {
        retrofit.create(FriendApiService::class.java)
    }
    
    val notificationApi: NotificationApiService by lazy {
        retrofit.create(NotificationApiService::class.java)
    }
}
