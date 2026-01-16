package com.kma.forumkma.core.utils

/**
 * App-wide constants
 */
object Constants {
    
    // API Configuration
    const val BASE_URL = "https://your-api-url.com/api/"
    const val TIMEOUT_CONNECT = 30L // seconds
    const val TIMEOUT_READ = 30L
    const val TIMEOUT_WRITE = 30L
    
    // SharedPreferences Keys
    const val PREF_NAME = "forum_kma_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_THEME_MODE = "theme_mode"
    
    // Database
    const val DATABASE_NAME = "forum_kma_db"
    const val DATABASE_VERSION = 1
    
    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5
    
    // Image Upload
    const val MAX_IMAGE_SIZE_MB = 5
    const val MAX_IMAGES_PER_POST = 10
    const val ALLOWED_IMAGE_EXTENSIONS = "jpg,jpeg,png,gif,webp"
    
    // Date Format
    const val DATE_FORMAT_FULL = "dd/MM/yyyy HH:mm:ss"
    const val DATE_FORMAT_SHORT = "dd/MM/yyyy"
    const val DATE_FORMAT_TIME = "HH:mm"
    
    // Navigation
    object Routes {
        const val SPLASH = "splash"
        const val ONBOARDING = "onboarding"
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val HOME = "home"
        const val POST_DETAIL = "post/{postId}"
        const val CREATE_POST = "create_post"
        const val PROFILE = "profile/{userId}"
        const val MESSAGES = "messages"
        const val CHAT = "chat/{conversationId}"
        const val SETTINGS = "settings"
    }
    
    // Error Messages
    object ErrorMessages {
        const val NETWORK_ERROR = "Lỗi kết nối mạng. Vui lòng kiểm tra lại!"
        const val SERVER_ERROR = "Lỗi server. Vui lòng thử lại sau!"
        const val UNKNOWN_ERROR = "Đã có lỗi xảy ra. Vui lòng thử lại!"
        const val UNAUTHORIZED = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!"
        const val VALIDATION_ERROR = "Dữ liệu không hợp lệ!"
    }
    
    // Validation
    object Validation {
        const val MIN_PASSWORD_LENGTH = 6
        const val MAX_PASSWORD_LENGTH = 32
        const val MIN_USERNAME_LENGTH = 3
        const val MAX_USERNAME_LENGTH = 20
        const val MAX_POST_CONTENT_LENGTH = 5000
        const val MAX_COMMENT_LENGTH = 1000
    }
}
