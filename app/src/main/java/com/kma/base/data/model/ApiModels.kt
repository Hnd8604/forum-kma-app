package com.kma.base.data.model

import com.google.gson.annotations.SerializedName

// Common API Response wrapper - FIXED to match backend
data class ApiResponse<T>(
    @SerializedName("code")
    val code: String,  // String, not Int!

    @SerializedName("message")
    val message: String,

    @SerializedName("result")  // "result", not "data"!
    val result: T?
)

// Page Response for paginated data
data class PageResponse<T>(
    @SerializedName("content")
    val content: List<T>,

    @SerializedName("page")
    val page: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("total")
    val total: Long,

    @SerializedName("totalPages")
    val totalPages: Int
)

// Auth Models - FIXED to match backend
data class LoginRequest(
    @SerializedName("username")  // username, not email!
    val username: String,

    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("username")  // username is required!
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("dob")
    val dob: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("address")
    val address: String? = null
)

// AuthResponse - NO user object, only tokens!
data class AuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("sessionId")
    val sessionId: String
)

// User Models - FIXED to match backend
data class UserResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("dob")
    val dob: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("roleId")
    val roleId: String? = null,

    @SerializedName("roleName")
    val roleName: String? = null,

    @SerializedName("userStatus")
    val userStatus: String? = null,

    @SerializedName("is2FAEnabled")
    val is2FAEnabled: Boolean? = null
) {
    val fullName: String
        get() = if (!firstName.isNullOrBlank() && !lastName.isNullOrBlank()) {
            "$firstName $lastName"
        } else {
            username
        }
}

// Post Models - UPDATED with resourceUrls (array)
data class PostResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("authorId")
    val authorId: String,

    @SerializedName("authorName")
    val authorName: String? = null,

    @SerializedName("groupId")
    val groupId: String? = null,

    @SerializedName("groupName")
    val groupName: String? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("resourceUrls")  // Array of URLs
    val resourceUrls: List<String>? = null,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class PostWithInteractionResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("authorId")
    val authorId: String,

    @SerializedName("authorName")
    val authorName: String? = null,

    @SerializedName("groupId")
    val groupId: String? = null,

    @SerializedName("groupName")
    val groupName: String? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("resourceUrls")  // Array of URLs
    val resourceUrls: List<String>? = null,

    @SerializedName("reactionCount")
    val reactionCount: Int = 0,

    @SerializedName("commentCount")
    val commentCount: Int = 0,

    @SerializedName("userReaction")
    val userReaction: String? = null,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class CreatePostRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("groupId")
    val groupId: String? = null,

    @SerializedName("type")
    val type: String = "TEXT",

    @SerializedName("resourceUrls")  // Array of URLs
    val resourceUrls: List<String>? = null
)

// Chat Models
data class ConversationResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("participantIds")
    val participantIds: List<String>,

    @SerializedName("participantNames")
    val participantNames: Map<String, String>? = null,

    @SerializedName("lastMessage")
    val lastMessage: String? = null,

    @SerializedName("lastMessageTime")
    val lastMessageTime: String? = null,

    @SerializedName("unreadCount")
    val unreadCount: Int = 0,

    @SerializedName("isGroup")
    val isGroup: Boolean = false,

    @SerializedName("groupName")
    val groupName: String? = null,

    @SerializedName("createdAt")
    val createdAt: String
)

data class ChatMessageResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("conversationId")
    val conversationId: String,

    @SerializedName("senderId")
    val senderId: String,

    @SerializedName("senderName")
    val senderName: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("read")
    val read: Boolean = false
)

data class SendMessageRequest(
    @SerializedName("conversationId")
    val conversationId: String,

    @SerializedName("content")
    val content: String
)
