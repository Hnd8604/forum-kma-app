package com.kma.forumkma.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Login Request DTO
 */
data class LoginRequestDto(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Register Request DTO
 */
data class RegisterRequestDto(
    @SerializedName("username")
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

/**
 * Refresh Token Request DTO
 */
data class RefreshRequestDto(
    @SerializedName("refreshToken")
    val refreshToken: String
)

/**
 * Logout Request DTO
 */
data class LogoutRequestDto(
    @SerializedName("sessionId")
    val sessionId: String
)
