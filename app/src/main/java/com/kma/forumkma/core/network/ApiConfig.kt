package com.kma.forumkma.core.network

/**
 * API Configuration
 * Thay đổi BASE_URL thành địa chỉ server của bạn
 */
object ApiConfig {
    // Thay đổi thành IP của server hoặc domain
    // Ví dụ: "http://10.0.2.2:8080/api/v1/" cho emulator (localhost)
    // Hoặc: "http://192.168.1.100:8080/api/v1/" cho thiết bị thật
    const val BASE_URL = "http://10.0.2.2:8080/api/v1/"
    
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
