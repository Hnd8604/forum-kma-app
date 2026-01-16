package com.kma.base.data.model

import com.google.gson.annotations.SerializedName

// Group Privacy/Visibility
enum class GroupPrivacy {
    PUBLIC,
    PRIVATE
}

// Member Role
enum class MemberRole {
    OWNER,
    ADMIN,
    MEMBER
}

// Group Response
data class GroupResponse(
    @SerializedName("groupId")
    val groupId: String,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("groupName")
    val groupName: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("privacy")
    val privacy: String? = null,

    @SerializedName("visibility")
    val visibility: String? = null,

    @SerializedName("memberCount")
    val memberCount: Int = 0,

    @SerializedName("postCount")
    val postCount: Int = 0,

    @SerializedName("createdBy")
    val createdBy: String? = null,

    @SerializedName("ownerId")
    val ownerId: String? = null,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    // Get display name (handle both name and groupName fields)
    val displayName: String
        get() = name ?: groupName ?: "Group"
    
    // Get privacy enum
    val groupPrivacy: GroupPrivacy?
        get() = try {
            GroupPrivacy.valueOf((privacy ?: visibility ?: "PUBLIC").uppercase())
        } catch (e: Exception) {
            GroupPrivacy.PUBLIC
        }
    
    val isPublic: Boolean
        get() = groupPrivacy == GroupPrivacy.PUBLIC
}

// Group Member
data class GroupMember(
    @SerializedName("id")
    val id: String,

    @SerializedName("groupId")
    val groupId: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("userEmail")
    val userEmail: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("role")
    val role: String,

    @SerializedName("joinedAt")
    val joinedAt: String
) {
    val memberRole: MemberRole?
        get() = try {
            MemberRole.valueOf(role.uppercase())
        } catch (e: Exception) {
            MemberRole.MEMBER
        }
    
    val isOwner: Boolean
        get() = memberRole == MemberRole.OWNER
    
    val isAdmin: Boolean
        get() = memberRole == MemberRole.ADMIN || memberRole == MemberRole.OWNER
}

// Group Member Check Response
data class GroupMemberCheck(
    @SerializedName("isMember")
    val isMember: Boolean = false,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("isOwner")
    val isOwner: Boolean = false,

    @SerializedName("isAdmin")
    val isAdmin: Boolean = false,

    @SerializedName("canPost")
    val canPost: Boolean = false,

    @SerializedName("canManageMembers")
    val canManageMembers: Boolean = false,

    @SerializedName("canManagePosts")
    val canManagePosts: Boolean = false
)

// Create Group Request
data class CreateGroupRequest(
    @SerializedName("groupName")
    val groupName: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("visibility")
    val visibility: String = "PUBLIC"
)

// Update Group Request
data class UpdateGroupRequest(
    @SerializedName("groupName")
    val groupName: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("visibility")
    val visibility: String? = null
)

// Join Group Request
data class JoinGroupRequest(
    @SerializedName("groupId")
    val groupId: String
)

// Update Member Role Request
data class UpdateMemberRoleRequest(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("role")
    val role: String
)

// Alias for API response
typealias GroupMembershipResponse = GroupMemberCheck
