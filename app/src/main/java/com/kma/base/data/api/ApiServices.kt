package com.kma.base.data.api

import com.google.gson.annotations.SerializedName
import com.kma.base.data.model.*
import okhttp3.MultipartBody
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
    suspend fun updateProfile(@Body request: UserUpdateRequest): ApiResponse<UserResponse>
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
    // POST /interactions - Create or update interaction (toggle like)
    @POST("interactions")
    suspend fun reactToPost(@Body request: Map<String, String>): ApiResponse<Any>
    
    // DELETE /interactions?postId=xxx - Remove interaction
    @DELETE("interactions")
    suspend fun removeReaction(@Query("postId") postId: String): ApiResponse<Any>
    
    // GET /interactions/count?postId=xxx - Get reaction counts
    @GET("interactions/count")
    suspend fun getPostReactions(@Query("postId") postId: String): ApiResponse<Map<String, Int>>
    
    // GET /interactions/my-reaction?postId=xxx - Get user's current reaction
    @GET("interactions/my-reaction")
    suspend fun getMyReaction(@Query("postId") postId: String): ApiResponse<Any>
}

interface CommentApiService {
    // GET /comments/post?postId=xxx&page=0&size=20 - Get comments for a post
    @GET("comments/post")
    suspend fun getCommentsByPost(
        @Query("postId") postId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20  // Backend uses "size" not "limit"
    ): ApiResponse<PageResponse<CommentResponse>>
    
    // GET /comments/{commentId}/replies - Get replies for a comment
    @GET("comments/{commentId}/replies")
    suspend fun getReplies(
        @Path("commentId") commentId: String
    ): ApiResponse<List<CommentResponse>>
    
    // POST /comments - Create a comment
    @POST("comments")
    suspend fun createComment(@Body request: Map<String, String>): ApiResponse<CommentResponse>
    
    // DELETE /comments/{id} - Delete a comment
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
    suspend fun getFriends(): ApiResponse<List<FriendResponse>>
    
    @GET("friends/requests/received")
    suspend fun getFriendRequests(): ApiResponse<List<FriendResponse>>
    
    @GET("friends/requests/sent")
    suspend fun getSentFriendRequests(): ApiResponse<List<FriendResponse>>
    
    @POST("friends/request/{userId}")
    suspend fun sendFriendRequest(@Path("userId") userId: String): ApiResponse<FriendResponse>
    
    @POST("friends/accept/{friendshipId}")
    suspend fun acceptFriendRequest(@Path("friendshipId") friendshipId: String): ApiResponse<FriendResponse>
    
    @POST("friends/reject/{friendshipId}")
    suspend fun rejectFriendRequest(@Path("friendshipId") friendshipId: String): ApiResponse<Any>
    
    @DELETE("friends/{userId}")
    suspend fun removeFriend(@Path("userId") userId: String): ApiResponse<Any>
    
    @GET("friends/status/{userId}")
    suspend fun checkFriendshipStatus(@Path("userId") userId: String): ApiResponse<FriendshipStatusResponse>
    
    @GET("friends/suggestions")
    suspend fun getSuggestedUsers(@Query("limit") limit: Int = 5): ApiResponse<List<FriendResponse>>
}

// Friendship Status Response
data class FriendshipStatusResponse(
    @SerializedName("status")
    val status: String,  // NONE, PENDING, ACCEPTED, BLOCKED
    
    @SerializedName("friendshipId")
    val friendshipId: String? = null,
    
    @SerializedName("isRequester")
    val isRequester: Boolean? = null
)

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

interface FileApiService {
    @Multipart
    @POST("files/upload/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): ApiResponse<UploadResponse>
    
    @Multipart
    @POST("files/upload/video")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part
    ): ApiResponse<UploadResponse>
    
    @Multipart
    @POST("files/upload/document")
    suspend fun uploadDocument(
        @Part file: MultipartBody.Part
    ): ApiResponse<UploadResponse>
    
    @Multipart
    @POST("files/upload/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): ApiResponse<UploadResponse>
    
    @DELETE("files/delete")
    suspend fun deleteFile(@Query("url") url: String): ApiResponse<Any>
}

// Upload Response
data class UploadResponse(
    @SerializedName("resourceUrl")
    val resourceUrl: String
)
