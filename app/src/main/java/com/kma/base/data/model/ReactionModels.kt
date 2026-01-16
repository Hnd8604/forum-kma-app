package com.kma.base.data.model

import com.google.gson.annotations.SerializedName

// Reaction Types - same as FE
enum class ReactionType(val displayName: String, val emoji: String) {
    LIKE("Thích", "👍"),
    LOVE("Yêu thích", "❤️"),
    HAHA("Haha", "😆"),
    WOW("Wow", "😮"),
    SAD("Buồn", "😢"),
    ANGRY("Phẫn nộ", "😠");
    
    companion object {
        fun fromString(value: String?): ReactionType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

// Interaction Response
data class InteractionResponse(
    @SerializedName("interactionId")
    val interactionId: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("postId")
    val postId: String,

    @SerializedName("commentId")
    val commentId: String? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("createdAt")
    val createdAt: String
)

// Create Interaction Request
data class CreateInteractionRequest(
    @SerializedName("postId")
    val postId: String,

    @SerializedName("commentId")
    val commentId: String? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("senderName")
    val senderName: String
)

// Interaction Count Response
data class InteractionCountResponse(
    @SerializedName("postId")
    val postId: String,

    @SerializedName("commentId")
    val commentId: String? = null,

    @SerializedName("likeCount")
    val likeCount: Int = 0,

    @SerializedName("loveCount")
    val loveCount: Int = 0,

    @SerializedName("hahaCount")
    val hahaCount: Int = 0,

    @SerializedName("wowCount")
    val wowCount: Int = 0,

    @SerializedName("sadCount")
    val sadCount: Int = 0,

    @SerializedName("angryCount")
    val angryCount: Int = 0,

    @SerializedName("totalCount")
    val totalCount: Int = 0
) {
    fun getCountByType(type: ReactionType): Int {
        return when (type) {
            ReactionType.LIKE -> likeCount
            ReactionType.LOVE -> loveCount
            ReactionType.HAHA -> hahaCount
            ReactionType.WOW -> wowCount
            ReactionType.SAD -> sadCount
            ReactionType.ANGRY -> angryCount
        }
    }
}
