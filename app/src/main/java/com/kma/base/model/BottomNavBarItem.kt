package com.kma.base.model

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.kma.base.NavScreen
import com.kma.base.R

sealed class BottomNavBarItem(
    val route: NavScreen,
    @get:StringRes val title: Int,
    val icon: ImageVector? = null,
    @get:RawRes val iconRes: Int? = null
) {
    data object Home : BottomNavBarItem(
        route = NavScreen.Home,
        title = R.string.home,
        icon = Icons.Default.Home
    )

    data object Messages : BottomNavBarItem(
        route = NavScreen.Messages,
        title = R.string.messages,
        iconRes = R.drawable.outline_chat_24
    )

    data object Profile : BottomNavBarItem(
        route = NavScreen.Profile,
        title = R.string.profile,
        icon = Icons.Default.Person
    )

    data object Settings : BottomNavBarItem(
        route = NavScreen.Settings,
        title = R.string.settings,
        icon = Icons.Default.Settings
    )
}