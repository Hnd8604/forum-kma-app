package com.kma.forumkma.data.mapper

import com.kma.forumkma.data.remote.dto.post.GroupResponseDto
import com.kma.forumkma.data.remote.dto.post.PostResponseDto
import com.kma.forumkma.domain.model.Group
import com.kma.forumkma.domain.model.Post
import com.kma.forumkma.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Mapper: PostResponseDto -> Post (Domain Model)
 */
object PostMapper {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    
    fun toDomain(dto: PostResponseDto): Post {
        // Create a simple User from author info in PostResponseDto
        val author = User(
            id = dto.authorId,
            username = dto.authorName ?: "Unknown",
            email = "",
            displayName = dto.authorName ?: "Unknown",
            avatarUrl = dto.authorAvatarUrl,
            bio = null,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        // Create Group if exists
        val group = if (!dto.groupId.isNullOrBlank()) {
            Group(
                id = dto.groupId,
                name = dto.groupName ?: "Unknown Group",
                description = null,
                icon = null,
                color = null
            )
        } else null
        
        return Post(
            id = dto.postId,
            author = author,
            content = dto.content,
            images = dto.resourceUrls ?: emptyList(),
            group = group,
            likesCount = dto.reactionCount,
            commentsCount = dto.commentCount.toInt(),
            sharesCount = 0,
            isLiked = false,
            isSaved = false,
            createdAt = parseDate(dto.createdAt),
            updatedAt = parseDate(dto.updatedAt)
        )
    }
    
    fun toDomainList(dtos: List<PostResponseDto>): List<Post> {
        return dtos.map { toDomain(it) }
    }
    
    private fun parseDate(dateStr: String?): Date {
        return try {
            dateStr?.let { dateFormat.parse(it) } ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }
}

/**
 * Mapper: GroupResponseDto -> Group (Domain Model)
 */
object GroupMapper {
    
    fun toDomain(dto: GroupResponseDto): Group {
        return Group(
            id = dto.groupId,
            name = dto.name,
            description = dto.description,
            icon = dto.iconUrl,
            color = null,
            membersCount = dto.memberCount
        )
    }
    
    fun toDomainList(dtos: List<GroupResponseDto>): List<Group> {
        return dtos.map { toDomain(it) }
    }
}
