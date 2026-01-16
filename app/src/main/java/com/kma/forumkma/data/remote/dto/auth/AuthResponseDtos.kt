package com.kma.forumkma.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Auth Response DTO (Login/Register response)
 */
data class AuthResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    
    @SerializedName("refreshToken")
    val refreshToken: String,
    
    @SerializedName("sessionId")
    val sessionId: String
)

/**
 * User Response DTO
 */
data class UserResponseDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("firstName")
    val firstName: String?,
    
    @SerializedName("lastName")
    val lastName: String?,
    
    @SerializedName("dob")
    val dob: String?,
    
    @SerializedName("gender")
    val gender: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    
    @SerializedName("roleId")
    val roleId: String?,
    
    @SerializedName("roleName")
    val roleName: String?,
    
    @SerializedName("userStatus")
    val userStatus: String?,
    
    @SerializedName("is2FAEnabled")
    val is2FAEnabled: Boolean = false,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)
