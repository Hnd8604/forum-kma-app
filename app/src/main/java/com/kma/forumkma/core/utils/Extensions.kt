package com.kma.forumkma.core.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension functions cho String
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.length >= Constants.Validation.MIN_PASSWORD_LENGTH
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

/**
 * Extension functions cho Date
 */
fun Date.toFormattedString(pattern: String = Constants.DATE_FORMAT_FULL): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun String.toDate(pattern: String = Constants.DATE_FORMAT_FULL): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.timeAgo(context: Context): String {
    val now = Date()
    val diff = now.time - this.time
    
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val months = days / 30
    val years = days / 365
    
    return when {
        seconds < 60 -> "Vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days < 7 -> "$days ngày trước"
        weeks < 4 -> "$weeks tuần trước"
        months < 12 -> "$months tháng trước"
        else -> "$years năm trước"
    }
}

/**
 * Extension functions cho Context
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Extension functions cho Number
 */
fun Int.formatCount(): String {
    return when {
        this < 1000 -> this.toString()
        this < 1_000_000 -> String.format("%.1fK", this / 1000.0)
        else -> String.format("%.1fM", this / 1_000_000.0)
    }
}

fun Long.formatFileSize(): String {
    val kb = this / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format("%.2f GB", gb)
        mb >= 1 -> String.format("%.2f MB", mb)
        kb >= 1 -> String.format("%.2f KB", kb)
        else -> "$this bytes"
    }
}

/**
 * Safe cast extension
 */
inline fun <reified T> Any?.safeCast(): T? = this as? T
