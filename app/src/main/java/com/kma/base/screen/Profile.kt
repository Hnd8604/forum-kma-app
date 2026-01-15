package com.kma.base.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.base.R

data class ProfileMenuItem(
    val icon: ImageVector? = null,
    val iconRes: Int? = null,
    val title: String,
    val subtitle: String? = null,
    val onClick: () -> Unit
)

data class UserStats(
    val posts: Int,
    val followers: Int,
    val following: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    // Sample user data
    val userName = "Nguyễn Văn A"
    val userEmail = "nguyenvana@kma.edu.vn"
    val userBio = "Sinh viên K67 - Khoa Công nghệ Thông tin"
    val userStats = UserStats(posts = 24, followers = 156, following = 89)
    
    val menuItems = listOf(
        ProfileMenuItem(
            icon = Icons.Default.Person,
            title = "Thông tin cá nhân",
            subtitle = "Xem và chỉnh sửa thông tin",
            onClick = { /* TODO */ }
        ),
        ProfileMenuItem(
            iconRes = R.drawable.baseline_article_24,
            title = "Bài viết của tôi",
            subtitle = "${userStats.posts} bài viết",
            onClick = { /* TODO */ }
        ),
        ProfileMenuItem(
            iconRes = R.drawable.baseline_bookmark_24,
            title = "Đã lưu",
            subtitle = "Bài viết đã lưu",
            onClick = { /* TODO */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.Favorite,
            title = "Đã thích",
            subtitle = "Bài viết đã thích",
            onClick = { /* TODO */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.Notifications,
            title = "Thông báo",
            subtitle = "Quản lý thông báo",
            onClick = { /* TODO */ }
        ),
        ProfileMenuItem(
            iconRes = R.drawable.baseline_security_24,
            title = "Bảo mật",
            subtitle = "Đổi mật khẩu, xác thực 2 lớp",
            onClick = { /* TODO */ }
        ),
        ProfileMenuItem(
            iconRes = R.drawable.baseline_help_24,
            title = "Trợ giúp & Hỗ trợ",
            subtitle = "Câu hỏi thường gặp, liên hệ",
            onClick = { /* TODO */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.Info,
            title = "Về Forum KMA",
            subtitle = "Phiên bản 1.0.0",
            onClick = { /* TODO */ }
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trang cá nhân",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit profile */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    userName = userName,
                    userEmail = userEmail,
                    userBio = userBio,
                    userStats = userStats
                )
            }
            
            // Divider
            item {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
            
            // Menu Items
            items(menuItems) { item ->
                ProfileMenuItemCard(item = item)
            }
            
            // Logout Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_logout_24),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng xuất",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    userName: String,
    userEmail: String,
    userBio: String,
    userStats: UserStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User Name
        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // User Email
        Text(
            text = userEmail,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // User Bio
        Text(
            text = userBio,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Bài viết", value = userStats.posts)
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            StatItem(label = "Người theo dõi", value = userStats.followers)
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            StatItem(label = "Đang theo dõi", value = userStats.following)
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuItemCard(
    item: ProfileMenuItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            when {
                item.icon != null -> {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                item.iconRes != null -> {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Arrow
        Icon(
            painter = painterResource(id = R.drawable.outline_arrow_forward_ios_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Box(
        modifier = modifier
            .width(1.dp)
            .background(color)
    )
}