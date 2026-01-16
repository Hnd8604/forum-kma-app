package com.kma.forumkma.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor để tự động thêm Authorization header
 */
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Lấy token từ storage
        val token = tokenProvider()
        
        // Nếu có token, thêm vào header
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
