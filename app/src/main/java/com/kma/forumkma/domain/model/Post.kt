package com.kma.forumkma.domain.model

import java.util.Date

/**
 * Domain model cho Post
 */
data class Post(
    val id: String,
    val author: User,
    val content: String,
    val images: List<String> = emptyList(),
    val group: Group? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val createdAt: Date,
    val updatedAt: Date
) {
    fun hasImages(): Boolean = images.isNotEmpty()
    
    fun hasGroup(): Boolean = group != null
    
    fun isOwnPost(currentUserId: String): Boolean = author.id == currentUserId
}

/**
 * Group/Category cho Post
 */
data class Group(
    val id: String,
    val name: String,
    val description: String?,
    val icon: String?,
    val color: String?,
    val membersCount: Int = 0
)
