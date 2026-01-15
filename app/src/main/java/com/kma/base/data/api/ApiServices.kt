package com.kma.base.data.api

import com.kma.base.data.model.*
import retrofit2.http.*

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>
    
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>
    
    @POST("api/v1/auth/logout")
    suspend fun logout(@Body sessionId: Map<String, String>): ApiResponse<String>
    
    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body refreshToken: Map<String, String>): ApiResponse<AuthResponse>
}

interface UserApiService {
    @GET("api/v1/users/me")
    suspend fun getMe(): ApiResponse<UserResponse>
    
    @GET("api/v1/users/{id}")
    suspend fun getUserById(@Path("id") userId: String): ApiResponse<UserResponse>
    
    @GET("api/v1/users/search")
    suspend fun searchUsers(
        @Query("keyword") keyword: String?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<UserResponse>>
    
    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: Map<String, Any>): ApiResponse<UserResponse>
}

interface PostApiService {
    @GET("api/v1/posts/feed")
    suspend fun getFeed(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "createdAt,DESC"
    ): ApiResponse<PageResponse<PostWithInteractionResponse>>
    
    @GET("api/v1/posts/feed/group/{groupId}")
    suspend fun getGroupFeed(
        @Path("groupId") groupId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "createdAt,DESC"
    ): ApiResponse<PageResponse<PostWithInteractionResponse>>
    
    @GET("api/v1/posts/{id}")
    suspend fun getPostById(@Path("id") postId: String): ApiResponse<PostResponse>
    
    @POST("api/v1/posts")
    suspend fun createPost(@Body request: CreatePostRequest): ApiResponse<PostResponse>
    
    @PUT("api/v1/posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: String,
        @Body request: Map<String, Any>
    ): ApiResponse<PostResponse>
    
    @DELETE("api/v1/posts/{id}")
    suspend fun deletePost(@Path("id") postId: String): ApiResponse<Void>
    
    @GET("api/v1/posts/author/{id}")
    suspend fun getPostsByAuthor(
        @Path("id") authorId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): ApiResponse<PageResponse<PostResponse>>
}

interface InteractionApiService {
    @POST("api/v1/interactions/react")
    suspend fun reactToPost(@Body request: Map<String, String>): ApiResponse<Any>
    
    @DELETE("api/v1/interactions/react")
    suspend fun removeReaction(@Query("postId") postId: String): ApiResponse<Any>
    
    @GET("api/v1/interactions/post/{postId}/reactions")
    suspend fun getPostReactions(@Path("postId") postId: String): ApiResponse<Map<String, Int>>
}

interface CommentApiService {
    @GET("api/v1/comments/post/{postId}")
    suspend fun getCommentsByPost(
        @Path("postId") postId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 20
    ): ApiResponse<PageResponse<Any>>
    
    @POST("api/v1/comments")
    suspend fun createComment(@Body request: Map<String, String>): ApiResponse<Any>
    
    @DELETE("api/v1/comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: String): ApiResponse<Void>
}

interface ChatApiService {
    @GET("api/v1/chat/conversations")
    suspend fun getConversations(): ApiResponse<List<ConversationResponse>>
    
    @GET("api/v1/chat/messages")
    suspend fun getMessages(@Query("conversationId") conversationId: String): ApiResponse<List<ChatMessageResponse>>
    
    @POST("api/v1/chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): ApiResponse<ChatMessageResponse>
    
    @POST("api/v1/chat/conversations/{conversationId}/mark-as-read")
    suspend fun markAsRead(@Path("conversationId") conversationId: String): ApiResponse<String>
}
