package com.kma.forumkma.data.mapper

import com.kma.forumkma.data.remote.dto.auth.UserResponseDto
import com.kma.forumkma.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Mapper: UserResponseDto -> User (Domain Model)
 */
object UserMapper {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    
    fun toDomain(dto: UserResponseDto): User {
        return User(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            displayName = buildDisplayName(dto),
            avatarUrl = dto.avatarUrl,
            bio = null,
            postsCount = 0,
            followersCount = 0,
            followingCount = 0,
            isFollowing = false,
            isFriend = false,
            createdAt = parseDate(dto.createdAt),
            updatedAt = parseDate(dto.updatedAt)
        )
    }
    
    private fun buildDisplayName(dto: UserResponseDto): String {
        val firstName = dto.firstName ?: ""
        val lastName = dto.lastName ?: ""
        val fullName = "$firstName $lastName".trim()
        return fullName.ifBlank { dto.username }
    }
    
    private fun parseDate(dateStr: String?): Date {
        return try {
            dateStr?.let { dateFormat.parse(it) } ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }
}
