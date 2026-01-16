package com.kma.forumkma.data.remote.api

import com.kma.forumkma.core.network.ApiResponse
import com.kma.forumkma.data.remote.dto.auth.*
import retrofit2.http.*

/**
 * Auth API Service
 * Endpoints cho authentication
 */
interface AuthApiService {
    
    /**
     * Đăng nhập
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): ApiResponse<AuthResponseDto>
    
    /**
     * Đăng ký
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): ApiResponse<AuthResponseDto>
    
    /**
     * Refresh token
     */
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequestDto): ApiResponse<AuthResponseDto>
    
    /**
     * Đăng xuất
     */
    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequestDto): ApiResponse<String>
    
    /**
     * Lấy thông tin user hiện tại
     */
    @GET("users/me")
    suspend fun getCurrentUser(): ApiResponse<UserResponseDto>
    
    /**
     * Lấy thông tin user theo ID
     */
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): ApiResponse<UserResponseDto>
    
    /**
     * Tìm kiếm users
     */
    @GET("users/search")
    suspend fun searchUsers(
        @Query("keyword") keyword: String?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<com.kma.forumkma.core.network.PageResponse<UserResponseDto>>
}
