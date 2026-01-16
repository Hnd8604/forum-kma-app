package com.kma.base.data.model

import com.google.gson.annotations.SerializedName

// Notification Types - same as FE
enum class NotificationType {
    POST,           // Bài đăng mới trong group
    LIKE_POST,      // Like bài đăng
    LIKE_COMMENT,   // Like comment
    COMMENT,        // Comment mới
    CHAT,           // Tin nhắn chat
    MENTION,        // Được mention
    ADMIN           // Thông báo từ admin
}

// Notification Response
data class NotificationResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("senderId")
    val senderId: String? = null,

    @SerializedName("senderName")
    val senderName: String? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    // Metadata
    @SerializedName("postId")
    val postId: String? = null,

    @SerializedName("commentId")
    val commentId: String? = null,

    @SerializedName("groupId")
    val groupId: String? = null,

    @SerializedName("interactionType")
    val interactionType: String? = null,

    @SerializedName("referenceId")
    val referenceId: String? = null,

    // Aggregated fields
    @SerializedName("aggregatedUserIds")
    val aggregatedUserIds: List<String>? = null,

    @SerializedName("aggregatedUserNames")
    val aggregatedUserNames: List<String>? = null,

    @SerializedName("lastActivityAt")
    val lastActivityAt: Any? = null,

    @SerializedName("isRead")
    val isRead: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: Any,  // Can be String or Array

    @SerializedName("readAt")
    val readAt: Any? = null
) {
    val notificationType: NotificationType?
        get() = try {
            NotificationType.valueOf(type)
        } catch (e: Exception) {
            null
        }
}

// Notification List Response
data class NotificationListResponse(
    @SerializedName("unreadCount")
    val unreadCount: Int,

    @SerializedName("data")
    val data: List<NotificationResponse>
)
