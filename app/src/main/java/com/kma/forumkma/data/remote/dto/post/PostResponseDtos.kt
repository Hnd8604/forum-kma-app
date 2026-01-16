package com.kma.forumkma.data.remote.dto.post

import com.google.gson.annotations.SerializedName

/**
 * Post Response DTO
 */
data class PostResponseDto(
    @SerializedName("postId")
    val postId: String,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("authorId")
    val authorId: String,
    
    @SerializedName("authorName")
    val authorName: String?,
    
    @SerializedName("authorAvatarUrl")
    val authorAvatarUrl: String?,
    
    @SerializedName("groupId")
    val groupId: String?,
    
    @SerializedName("groupName")
    val groupName: String?,
    
    @SerializedName("status")
    val status: String?, // PENDING, APPROVED, REJECTED
    
    @SerializedName("reactionCount")
    val reactionCount: Int = 0,
    
    @SerializedName("commentCount")
    val commentCount: Long = 0,
    
    @SerializedName("type")
    val type: String = "TEXT", // TEXT, IMAGE, DOC
    
    @SerializedName("resourceUrls")
    val resourceUrls: List<String>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

/**
 * Comment Response DTO
 */
data class CommentResponseDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("postId")
    val postId: String,
    
    @SerializedName("authorId")
    val authorId: String,
    
    @SerializedName("authorName")
    val authorName: String?,
    
    @SerializedName("authorAvatarUrl")
    val authorAvatarUrl: String?,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentId")
    val parentId: String?,
    
    @SerializedName("reactionCount")
    val reactionCount: Int = 0,
    
    @SerializedName("userReacted")
    val userReacted: Boolean = false,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

/**
 * Group Response DTO
 */
data class GroupResponseDto(
    @SerializedName("groupId")
    val groupId: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("iconUrl")
    val iconUrl: String?,
    
    @SerializedName("memberCount")
    val memberCount: Int = 0,
    
    @SerializedName("postCount")
    val postCount: Int = 0,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)
