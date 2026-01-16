package com.kma.forumkma.core.network.interceptors

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Logging interceptor factory
 * Uses Android Log instead of Timber for better compatibility
 */
object LoggingInterceptorFactory {
    
    private const val TAG = "OkHttp"
    
    fun create(isDebug: Boolean = true): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            // Use Android Log instead of Timber
            Log.d(TAG, message)
        }.apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    /**
     * Alternative: If you want to use Timber, uncomment this and comment the above
     */
    /*
    fun createWithTimber(isDebug: Boolean = true): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            timber.log.Timber.tag(TAG).d(message)
        }.apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    */
}
