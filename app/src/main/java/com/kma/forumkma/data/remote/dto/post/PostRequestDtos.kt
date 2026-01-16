package com.kma.forumkma.data.remote.dto.post

import com.google.gson.annotations.SerializedName

/**
 * Create Post Request DTO
 */
data class CreatePostRequestDto(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("groupId")
    val groupId: String,
    
    @SerializedName("type")
    val type: String = "TEXT", // TEXT, IMAGE, DOC
    
    @SerializedName("resourceUrls")
    val resourceUrls: List<String> = emptyList()
)

/**
 * Update Post Request DTO
 */
data class UpdatePostRequestDto(
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("content")
    val content: String?,
    
    @SerializedName("type")
    val type: String?,
    
    @SerializedName("resourceUrls")
    val resourceUrls: List<String>?
)

/**
 * Create Comment Request DTO
 */
data class CreateCommentRequestDto(
    @SerializedName("postId")
    val postId: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentId")
    val parentId: String? = null
)

/**
 * Interaction (Like/Reaction) Request DTO
 */
data class InteractionRequestDto(
    @SerializedName("targetId")
    val targetId: String,
    
    @SerializedName("targetType")
    val targetType: String, // POST, COMMENT
    
    @SerializedName("type")
    val type: String = "LIKE" // LIKE, LOVE, etc.
)
