package com.kma.base

import kotlinx.serialization.Serializable

sealed class NavScreen {
    @Serializable
    object Login : NavScreen()
    
    @Serializable
    object Register : NavScreen()
    
    @Serializable
    object Home : NavScreen ()

    @Serializable
    object Messages : NavScreen ()

    @Serializable
    object Profile : NavScreen ()

    @Serializable
    object Settings : NavScreen ()
}