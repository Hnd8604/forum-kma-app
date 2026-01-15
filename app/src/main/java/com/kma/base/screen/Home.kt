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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.base.R
import java.text.SimpleDateFormat
import java.util.*

data class Post(
    val id: String,
    val authorName: String,
    val authorAvatar: Color,
    val timestamp: Long,
    val title: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Mới nhất", "Phổ biến", "Theo dõi")
    
    // Sample posts data
    val samplePosts = remember {
        listOf(
            Post(
                id = "1",
                authorName = "Nguyễn Văn A",
                authorAvatar = Color(0xFF6200EE),
                timestamp = System.currentTimeMillis() - 1800000,
                title = "Hướng dẫn cài đặt Android Studio trên Windows 11",
                content = "Mình vừa viết một bài hướng dẫn chi tiết về cách cài đặt Android Studio trên Windows 11. Các bạn có thể tham khảo nhé!",
                likes = 24,
                comments = 8,
                category = "Lập trình"
            ),
            Post(
                id = "2",
                authorName = "Trần Thị B",
                authorAvatar = Color(0xFF03DAC6),
                timestamp = System.currentTimeMillis() - 3600000,
                title = "Tìm bạn làm đồ án TTCS",
                content = "Mình đang tìm bạn cùng làm đồ án TTCS về ứng dụng quản lý thư viện. Ai có hứng thú thì inbox mình nhé!",
                likes = 15,
                comments = 12,
                category = "Học tập"
            ),
            Post(
                id = "3",
                authorName = "Lê Văn C",
                authorAvatar = Color(0xFFFF6B6B),
                timestamp = System.currentTimeMillis() - 7200000,
                title = "Chia sẻ tài liệu môn Cơ sở dữ liệu",
                content = "Mình có tổng hợp tài liệu môn CSDL từ thầy Nguyễn Văn X. Ai cần thì comment bên dưới nhé!",
                likes = 42,
                comments = 18,
                category = "Tài liệu"
            ),
            Post(
                id = "4",
                authorName = "Phạm Thị D",
                authorAvatar = Color(0xFF4ECDC4),
                timestamp = System.currentTimeMillis() - 10800000,
                title = "Thông báo: Lịch thi giữa kỳ học kỳ 1",
                content = "Nhà trường vừa công bố lịch thi giữa kỳ. Các bạn chú ý kiểm tra lịch thi của mình nhé!",
                likes = 67,
                comments = 23,
                category = "Thông báo"
            ),
            Post(
                id = "5",
                authorName = "Hoàng Văn E",
                authorAvatar = Color(0xFFFFBE0B),
                timestamp = System.currentTimeMillis() - 14400000,
                title = "Review khóa học Kotlin cho người mới bắt đầu",
                content = "Mình vừa hoàn thành khóa học Kotlin trên Udemy. Đây là review chi tiết của mình về khóa học này...",
                likes = 31,
                comments = 9,
                category = "Review"
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Forum KMA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Create new post */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            
            // Posts List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(samplePosts) { post ->
                    PostCard(
                        post = post,
                        onClick = {
                            // TODO: Navigate to post detail
                        }
                    )
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Author Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(post.authorAvatar),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.authorName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${formatPostTimestamp(post.timestamp)} • ${post.category}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = { /* TODO: More options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Post Title
        Text(
            text = post.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Post Content
        Text(
            text = post.content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO: Like */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.likes.toString(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO: Comment */ }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_mode_comment_24),
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.comments.toString(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Share Button
            IconButton(
                onClick = { /* TODO: Share */ },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun formatPostTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Vừa xong"
        diff < 3600000 -> "${diff / 60000} phút trước"
        diff < 86400000 -> "${diff / 3600000} giờ trước"
        diff < 604800000 -> "${diff / 86400000} ngày trước"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}