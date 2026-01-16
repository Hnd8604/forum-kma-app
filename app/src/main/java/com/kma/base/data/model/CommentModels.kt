package com.kma.base.data.model

import com.google.gson.annotations.SerializedName

// Comment Types
enum class CommentType {
    TEXT, IMAGE, VIDEO, DOC
}

// Comment Response từ API
data class CommentResponse(
    @SerializedName("commentId")
    val commentId: String,

    @SerializedName("postId")
    val postId: String,

    @SerializedName("authorId")
    val authorId: String,

    @SerializedName("authorName")
    val authorName: String? = null,

    @SerializedName("authorAvatarUrl")
    val authorAvatarUrl: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("parentCommentId")
    val parentCommentId: String? = null,

    @SerializedName("type")
    val type: String? = "TEXT",

    @SerializedName("resourceUrls")
    val resourceUrls: List<String>? = null,

    @SerializedName("replyCount")
    val replyCount: Int = 0,

    @SerializedName("reactionCount")
    val reactionCount: Int = 0,

    @SerializedName("userReactionType")  // Fixed: backend returns "userReactionType"
    val myReaction: String? = null,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    val isReply: Boolean
        get() = parentCommentId != null
    
    val hasMedia: Boolean
        get() = !resourceUrls.isNullOrEmpty()
}

// Create Comment Request
data class CreateCommentRequest(
    @SerializedName("postId")
    val postId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("parentCommentId")
    val parentCommentId: String? = null,

    @SerializedName("senderName")
    val senderName: String? = null,

    @SerializedName("type")
    val type: String = "TEXT",

    @SerializedName("urls")
    val urls: List<String>? = null
)

// Update Comment Request
data class UpdateCommentRequest(
    @SerializedName("content")
    val content: String,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("urls")
    val urls: List<String>? = null
)
