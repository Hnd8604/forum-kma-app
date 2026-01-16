package com.kma.base.data.model

import com.google.gson.annotations.SerializedName

// Friendship Status
enum class FriendshipStatus {
    PENDING,    // Đang chờ xác nhận
    ACCEPTED,   // Đã là bạn bè
    REJECTED,   // Đã từ chối
    BLOCKED     // Đã chặn
}

// Friend Response - matches backend FriendshipResponse
data class FriendResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("acceptedAt")
    val acceptedAt: String? = null,

    @SerializedName("isRequester")
    val isRequester: Boolean? = null
) {
    val friendshipStatus: FriendshipStatus?
        get() = try {
            FriendshipStatus.valueOf(status)
        } catch (e: Exception) {
            null
        }

    val fullName: String
        get() = if (!firstName.isNullOrBlank() && !lastName.isNullOrBlank()) {
            "$lastName $firstName"
        } else {
            username
        }
}

// Friend Info with User details
data class FriendInfo(
    @SerializedName("friendshipId")
    val friendshipId: String? = null,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("isFriend")
    val isFriend: Boolean = false,

    @SerializedName("mutualFriendsCount")
    val mutualFriendsCount: Int = 0
) {
    val fullName: String
        get() = if (!firstName.isNullOrBlank() && !lastName.isNullOrBlank()) {
            "$firstName $lastName"
        } else {
            username
        }
}

// Friend Request
data class FriendRequest(
    @SerializedName("id")
    val id: String,

    @SerializedName("fromUserId")
    val fromUserId: String,

    @SerializedName("fromUserName")
    val fromUserName: String? = null,

    @SerializedName("fromUserAvatar")
    val fromUserAvatar: String? = null,

    @SerializedName("toUserId")
    val toUserId: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("createdAt")
    val createdAt: String
)

// Send Friend Request
data class SendFriendRequest(
    @SerializedName("friendId")
    val friendId: String
)

// Accept/Reject Friend Request
data class RespondFriendRequest(
    @SerializedName("friendshipId")
    val friendshipId: String,

    @SerializedName("accept")
    val accept: Boolean
)

// Alias for API response
typealias FriendRequestResponse = FriendRequest
