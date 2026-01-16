package com.kma.forumkma.core.network

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response wrapper từ Backend
 */
data class ApiResponse<T>(
    @SerializedName("code")
    val code: Int = 200,
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("data")
    val data: T? = null
) {
    val isSuccess: Boolean
        get() = code == 200 || code == 0
}

/**
 * Paginated response
 */
data class PageResponse<T>(
    @SerializedName("items")
    val items: List<T> = emptyList(),
    
    @SerializedName("page")
    val page: Int = 0,
    
    @SerializedName("limit")
    val limit: Int = 10,
    
    @SerializedName("total")
    val total: Long = 0,
    
    @SerializedName("totalPages")
    val totalPages: Int = 0
)
