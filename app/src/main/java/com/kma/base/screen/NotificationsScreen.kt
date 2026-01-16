package com.kma.base.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.base.data.model.NotificationResponse
import com.kma.base.data.model.NotificationType

// Colors
private val PrimaryBlue = Color(0xFF1877F2)
private val LightBlue = Color(0xFFE7F3FF)
private val LightGray = Color(0xFFF0F2F5)
private val DarkGray = Color(0xFF65676B)
private val UnreadBg = Color(0xFFE7F3FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (NotificationResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    var notifications by remember { mutableStateOf<List<NotificationResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var unreadCount by remember { mutableStateOf(0) }

    // TODO: Load notifications from repository
    LaunchedEffect(Unit) {
        // Load notifications
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Thông báo")
                        if (unreadCount > 0) {
                            Text(
                                text = "$unreadCount chưa đọc",
                                fontSize = 12.sp,
                                color = DarkGray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Mark all as read */ }) {
                        Icon(Icons.Default.Check, contentDescription = "Mark all read")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (notifications.isEmpty()) {
            EmptyNotifications(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { onNotificationClick(notification) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: NotificationResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (!notification.isRead) UnreadBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon based on type
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(getNotificationColor(notification.notificationType)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getNotificationIcon(notification.notificationType),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Title
            Text(
                text = notification.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Content
            Text(
                text = notification.content,
                color = DarkGray,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Time
            Text(
                text = formatNotificationTime(notification.createdAt.toString()),
                color = if (!notification.isRead) PrimaryBlue else DarkGray,
                fontSize = 12.sp,
                fontWeight = if (!notification.isRead) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        // Unread indicator
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
            )
        }
    }
}

@Composable
private fun EmptyNotifications(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Không có thông báo",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Các thông báo mới sẽ hiển thị ở đây",
                color = DarkGray,
                fontSize = 14.sp
            )
        }
    }
}

private fun getNotificationIcon(type: NotificationType?): ImageVector {
    return when (type) {
        NotificationType.POST -> Icons.Default.MailOutline
        NotificationType.LIKE_POST, NotificationType.LIKE_COMMENT -> Icons.Default.Favorite
        NotificationType.COMMENT -> Icons.Default.Email
        NotificationType.CHAT -> Icons.Default.Email
        NotificationType.MENTION -> Icons.Default.Person
        NotificationType.ADMIN -> Icons.Default.Settings
        else -> Icons.Default.Notifications
    }
}

private fun getNotificationColor(type: NotificationType?): Color {
    return when (type) {
        NotificationType.POST -> Color(0xFF4CAF50)
        NotificationType.LIKE_POST, NotificationType.LIKE_COMMENT -> Color(0xFFE91E63)
        NotificationType.COMMENT -> Color(0xFF2196F3)
        NotificationType.CHAT -> Color(0xFF9C27B0)
        NotificationType.MENTION -> Color(0xFFFF9800)
        NotificationType.ADMIN -> Color(0xFF607D8B)
        else -> Color(0xFF1877F2)
    }
}

private fun formatNotificationTime(time: String): String {
    // TODO: Implement proper time formatting
    return time
}
