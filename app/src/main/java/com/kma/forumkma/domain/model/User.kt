package com.kma.forumkma.domain.model

import java.util.Date

/**
 * Domain model cho User
 * Đây là model thuần túy, không phụ thuộc vào Android framework
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val isFriend: Boolean = false,
    val createdAt: Date,
    val updatedAt: Date
) {
    val initials: String
        get() = displayName.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
    
    fun hasAvatar(): Boolean = !avatarUrl.isNullOrBlank()
}
