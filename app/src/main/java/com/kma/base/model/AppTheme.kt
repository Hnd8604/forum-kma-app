package com.kma.base.model

sealed class AppTheme(val name: String) {
    data object LIGHT : AppTheme("Light")
    data object DARK : AppTheme("Dark")
    data object SYSTEM : AppTheme("System")
}