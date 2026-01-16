package com.kma.base.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request body for updating user profile
 */
data class UserUpdateRequest(
    @SerializedName("firstName")
    val firstName: String? = null,
    
    @SerializedName("lastName")
    val lastName: String? = null,
    
    @SerializedName("dob")
    val dob: String? = null,
    
    @SerializedName("gender")
    val gender: String? = null,
    
    @SerializedName("address")
    val address: String? = null,
    
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null
)
