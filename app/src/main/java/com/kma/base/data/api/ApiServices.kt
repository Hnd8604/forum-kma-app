package com.kma.base.data.api

import com.kma.base.data.model.*
import retrofit2.http.*

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>
    
    @POST("auth/logout")
    suspend fun logout(@Body sessionId: Map<String, String>): ApiResponse<String>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: Map<String, String>): ApiResponse<AuthResponse>
}

interface UserApiService {
    @GET("users/me")
    suspend fun getMe(): ApiResponse<UserResponse>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: String): ApiResponse<UserResponse>
    
    @GET("users/search")
    suspend fun searchUsers(
        @Query("keyword") keyword: String?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<UserResponse>>
    
    @PUT("users/me")
    suspend fun updateProfile(@Body request: Map<String, Any>): ApiResponse<UserResponse>
}

interface PostApiService {
    @GET("posts/feed")
    suspend fun getFeed(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "createdAt,DESC"
    ): ApiResponse<PageResponse<PostWithInteractionResponse>>
    
    @GET("posts/feed/group/{groupId}")
    suspend fun getGroupFeed(
        @Path("groupId") groupId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "createdAt,DESC"
    ): ApiResponse<PageResponse<PostWithInteractionResponse>>
    
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") postId: String): ApiResponse<PostResponse>
    
    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): ApiResponse<PostResponse>
    
    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: String,
        @Body request: Map<String, Any>
    ): ApiResponse<PostResponse>
    
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") postId: String): ApiResponse<Void>
    
    @GET("posts/author/{id}")
    suspend fun getPostsByAuthor(
        @Path("id") authorId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): ApiResponse<PageResponse<PostResponse>>
}

interface InteractionApiService {
    @POST("interactions/react")
    suspend fun reactToPost(@Body request: Map<String, String>): ApiResponse<Any>
    
    @DELETE("interactions/react")
    suspend fun removeReaction(@Query("postId") postId: String): ApiResponse<Any>
    
    @GET("interactions/post/{postId}/reactions")
    suspend fun getPostReactions(@Path("postId") postId: String): ApiResponse<Map<String, Int>>
}

interface CommentApiService {
    @GET("comments/post/{postId}")
    suspend fun getCommentsByPost(
        @Path("postId") postId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 20
    ): ApiResponse<PageResponse<Any>>
    
    @POST("comments")
    suspend fun createComment(@Body request: Map<String, String>): ApiResponse<Any>
    
    @DELETE("comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: String): ApiResponse<Void>
}

interface ChatApiService {
    @GET("chat/conversations")
    suspend fun getConversations(): ApiResponse<List<ConversationResponse>>
    
    @GET("chat/messages")
    suspend fun getMessages(
        @Query("conversationId") conversationId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 20
    ): ApiResponse<PageResponse<ChatMessageResponse>>
    
    @POST("chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): ApiResponse<ChatMessageResponse>
    
    @POST("chat/conversations/{conversationId}/mark-as-read")
    suspend fun markAsRead(@Path("conversationId") conversationId: String): ApiResponse<String>
    
    @DELETE("chat/messages/{messageId}")
    suspend fun deleteMessage(@Path("messageId") messageId: String): ApiResponse<Void>
}

interface GroupApiService {
    @GET("groups")
    suspend fun getAllGroups(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String = ""
    ): ApiResponse<PageResponse<GroupResponse>>
    
    @GET("groups/{groupId}")
    suspend fun getGroupById(@Path("groupId") groupId: String): ApiResponse<GroupResponse>
    
    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): ApiResponse<GroupResponse>
    
    @PUT("groups/{groupId}")
    suspend fun updateGroup(
        @Path("groupId") groupId: String,
        @Body request: Map<String, Any>
    ): ApiResponse<GroupResponse>
    
    @DELETE("groups/{groupId}")
    suspend fun deleteGroup(@Path("groupId") groupId: String): ApiResponse<Void>
    
    @GET("groups/my-groups")
    suspend fun getMyGroups(): ApiResponse<List<GroupResponse>>
    
    @POST("groups/join")
    suspend fun joinGroup(@Body request: Map<String, String>): ApiResponse<String>
    
    @POST("groups/leave/{groupId}")
    suspend fun leaveGroup(@Path("groupId") groupId: String): ApiResponse<String>
    
    @GET("groups/{groupId}/membership")
    suspend fun checkMembership(@Path("groupId") groupId: String): ApiResponse<GroupMembershipResponse>
}

interface FriendApiService {
    @GET("friends")
    suspend fun getFriends(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<FriendResponse>>
    
    @GET("friends/requests")
    suspend fun getFriendRequests(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<FriendRequestResponse>>
    
    @POST("friends/request")
    suspend fun sendFriendRequest(@Body request: Map<String, String>): ApiResponse<Any>
    
    @POST("friends/accept/{requestId}")
    suspend fun acceptFriendRequest(@Path("requestId") requestId: String): ApiResponse<Any>
    
    @POST("friends/reject/{requestId}")
    suspend fun rejectFriendRequest(@Path("requestId") requestId: String): ApiResponse<Any>
    
    @DELETE("friends/{friendId}")
    suspend fun removeFriend(@Path("friendId") friendId: String): ApiResponse<Any>
}

interface NotificationApiService {
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<NotificationResponse>>
    
    @POST("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") notificationId: String): ApiResponse<Any>
    
    @POST("notifications/read-all")
    suspend fun markAllAsRead(): ApiResponse<Any>
    
    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): ApiResponse<Int>
}
