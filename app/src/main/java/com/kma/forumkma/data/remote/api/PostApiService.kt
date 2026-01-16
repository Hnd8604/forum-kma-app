package com.kma.forumkma.data.remote.api

import com.kma.forumkma.core.network.ApiResponse
import com.kma.forumkma.core.network.PageResponse
import com.kma.forumkma.data.remote.dto.post.*
import retrofit2.http.*

/**
 * Post API Service
 * Endpoints cho posts, comments, interactions
 */
interface PostApiService {
    
    // ============ POSTS ============
    
    /**
     * Lấy danh sách tất cả posts
     */
    @GET("posts")
    suspend fun getAllPosts(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String = ""
    ): ApiResponse<PageResponse<PostResponseDto>>
    
    /**
     * Lấy chi tiết post
     */
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: String): ApiResponse<PostResponseDto>
    
    /**
     * Tạo post mới
     */
    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequestDto): ApiResponse<PostResponseDto>
    
    /**
     * Cập nhật post
     */
    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") id: String,
        @Body request: UpdatePostRequestDto
    ): ApiResponse<PostResponseDto>
    
    /**
     * Xóa post
     */
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: String): ApiResponse<Void>
    
    /**
     * Lấy posts của user
     */
    @GET("posts/author/{authorId}")
    suspend fun getPostsByAuthor(
        @Path("authorId") authorId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String = "",
        @Query("status") status: String? = null
    ): ApiResponse<PageResponse<PostResponseDto>>
    
    /**
     * Lấy posts trong group
     */
    @GET("posts/group/{groupId}")
    suspend fun getPostsByGroup(
        @Path("groupId") groupId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): ApiResponse<PageResponse<PostResponseDto>>
    
    // ============ COMMENTS ============
    
    /**
     * Lấy comments của post
     */
    @GET("comments/post")
    suspend fun getCommentsByPost(
        @Query("postId") postId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<CommentResponseDto>>
    
    /**
     * Tạo comment
     */
    @POST("comments")
    suspend fun createComment(@Body request: CreateCommentRequestDto): ApiResponse<CommentResponseDto>
    
    /**
     * Xóa comment
     */
    @DELETE("comments/{id}")
    suspend fun deleteComment(@Path("id") id: String): ApiResponse<Void>
    
    // ============ INTERACTIONS ============
    
    /**
     * Like/React post hoặc comment
     */
    @POST("interactions")
    suspend fun addInteraction(@Body request: InteractionRequestDto): ApiResponse<Any>
    
    /**
     * Remove reaction
     */
    @DELETE("interactions/{targetId}")
    suspend fun removeInteraction(
        @Path("targetId") targetId: String,
        @Query("targetType") targetType: String
    ): ApiResponse<Void>
    
    // ============ GROUPS ============
    
    /**
     * Lấy danh sách groups
     */
    @GET("groups")
    suspend fun getAllGroups(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<GroupResponseDto>>
    
    /**
     * Lấy chi tiết group
     */
    @GET("groups/{id}")
    suspend fun getGroupById(@Path("id") id: String): ApiResponse<GroupResponseDto>
}
