package com.kma.base.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kma.base.R
import com.kma.base.data.model.FriendResponse
import com.kma.base.data.model.PostResponse
import com.kma.base.data.model.UserResponse
import com.kma.base.viewmodel.ProfileTab
import com.kma.base.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userId: String? = null,
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToPost: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    
    // Load profile on first composition
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUserProfile(userId)
        } else {
            viewModel.loadCurrentUser()
        }
    }
    
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
                    if (state.isOwnProfile) {
                        IconButton(onClick = onNavigateToEditProfile) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Chỉnh sửa"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Có lỗi xảy ra",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            state.user != null -> {
                ProfileContent(
                    modifier = modifier.padding(paddingValues),
                    state = state,
                    viewModel = viewModel,
                    onLogout = onLogout,
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToPost = onNavigateToPost
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    state: com.kma.base.viewmodel.ProfileState,
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToPost: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Profile Header
        item {
            ProfileHeaderSection(
                user = state.user!!,
                displayName = viewModel.getDisplayName(),
                initials = viewModel.getInitials(),
                postCount = state.postCount,
                friendCount = state.friendCount,
                isOwnProfile = state.isOwnProfile,
                onChatClick = { onNavigateToChat(state.user.id) }
            )
        }
        
        // Tab Bar
        item {
            ProfileTabBar(
                activeTab = state.activeTab,
                onTabChange = { viewModel.setActiveTab(it) }
            )
        }
        
        // Tab Content
        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }
        
        when (state.activeTab) {
            ProfileTab.ABOUT -> {
                item {
                    AboutSection(
                        user = state.user!!,
                        genderText = viewModel.getGenderText()
                    )
                }
            }
            ProfileTab.POSTS -> {
                if (state.isLoadingPosts) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (state.posts.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.Create,
                            message = "Chưa có bài viết nào"
                        )
                    }
                } else {
                    items(state.posts) { post ->
                        ProfilePostItem(
                            post = post,
                            onClick = { onNavigateToPost(post.id) },
                            onLikeClick = { /* TODO: Implement like */ },
                            onCommentClick = { onNavigateToPost(post.id) }
                        )
                    }
                }
            }
            ProfileTab.FRIENDS -> {
                if (state.isLoadingFriends) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (state.friends.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.Person,
                            message = "Chưa có bạn bè nào"
                        )
                    }
                } else {
                    items(state.friends) { friend ->
                        FriendItem(friend = friend)
                    }
                }
            }
        }
        
        // Logout Button (only for own profile)
        if (state.isOwnProfile) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
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
private fun ProfileHeaderSection(
    user: UserResponse,
    displayName: String,
    initials: String,
    postCount: Int,
    friendCount: Int,
    isOwnProfile: Boolean,
    onChatClick: () -> Unit
) {
    Column(
        modifier = Modifier
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
            if (!user.avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Display Name
        Text(
            text = displayName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Username
        Text(
            text = "@${user.username}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Bài viết", value = postCount)
            StatItem(label = "Bạn bè", value = friendCount)
        }
        
        // Chat button for other users
        if (!isOwnProfile) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onChatClick,
                modifier = Modifier.fillMaxWidth(0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nhắn tin")
            }
        }
    }
}

@Composable
private fun StatItem(
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
private fun ProfileTabBar(
    activeTab: ProfileTab,
    onTabChange: (ProfileTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProfileTabButton(
            text = "Giới thiệu",
            icon = Icons.Default.Person,
            isSelected = activeTab == ProfileTab.ABOUT,
            onClick = { onTabChange(ProfileTab.ABOUT) },
            modifier = Modifier.weight(1f)
        )
        ProfileTabButton(
            text = "Bài viết",
            icon = Icons.Default.Create,
            isSelected = activeTab == ProfileTab.POSTS,
            onClick = { onTabChange(ProfileTab.POSTS) },
            modifier = Modifier.weight(1f)
        )
        ProfileTabButton(
            text = "Bạn bè",
            icon = Icons.Default.AccountCircle,
            isSelected = activeTab == ProfileTab.FRIENDS,
            onClick = { onTabChange(ProfileTab.FRIENDS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileTabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(2.dp)
                .background(
                    if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent
                )
        )
    }
}

@Composable
private fun AboutSection(
    user: UserResponse,
    genderText: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Contact Information
        Text(
            text = "Thông tin liên hệ",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        AboutInfoRow(
            icon = Icons.Default.Email,
            label = "Email",
            value = user.email
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Basic Information
        Text(
            text = "Thông tin cơ bản",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        if (genderText != null) {
            AboutInfoRow(
                icon = Icons.Default.Person,
                label = "Giới tính",
                value = genderText
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (!user.dob.isNullOrBlank()) {
            AboutInfoRow(
                icon = Icons.Default.DateRange,
                label = "Ngày sinh",
                value = formatDate(user.dob)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (!user.address.isNullOrBlank()) {
            AboutInfoRow(
                icon = Icons.Default.LocationOn,
                label = "Địa chỉ",
                value = user.address
            )
        }
        
        // Status
        if (!user.userStatus.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            AboutInfoRow(
                icon = Icons.Default.CheckCircle,
                label = "Trạng thái",
                value = when (user.userStatus) {
                    "ACTIVE" -> "Đang hoạt động"
                    "INACTIVE" -> "Không hoạt động"
                    else -> user.userStatus
                }
            )
        }
    }
}

@Composable
private fun AboutInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ProfilePostItem(
    post: PostResponse,
    onClick: () -> Unit,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {}
) {
    val PrimaryBlue = Color(0xFF1877F2)
    val DarkGray = Color(0xFF65676B)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Author row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    if (!post.authorAvatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = post.authorAvatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = (post.authorName?.firstOrNull() ?: "U").toString().uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName ?: "Unknown",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatDateTime(post.createdAt),
                            color = DarkGray,
                            fontSize = 12.sp
                        )
                        if (post.groupName != null) {
                            Text(" • ", color = DarkGray, fontSize = 12.sp)
                            Text(
                                text = post.groupName,
                                color = PrimaryBlue,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                
                IconButton(onClick = { /* More options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            if (post.title.isNotBlank()) {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Content
            Text(
                text = post.content,
                fontSize = 14.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            
            // Media - Images
            if (!post.resourceUrls.isNullOrEmpty() && post.type == "IMAGE") {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = post.resourceUrls.first(),
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                if (post.resourceUrls.size > 1) {
                    Text(
                        text = "+${post.resourceUrls.size - 1} ảnh khác",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Media - Video
            if (!post.resourceUrls.isNullOrEmpty() && post.type == "VIDEO") {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = post.resourceUrls.first(),
                        contentDescription = "Video",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Likes
                if (post.reactionCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.reactionCount}",
                            color = DarkGray,
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Comments
                if (post.commentCount > 0) {
                    Text(
                        text = "${post.commentCount} bình luận",
                        color = DarkGray,
                        fontSize = 12.sp
                    )
                }
            }
            
            HorizontalDivider()
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Like button
                TextButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = DarkGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thích", color = DarkGray)
                }
                
                // Comment button
                TextButton(onClick = onCommentClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Comment",
                        tint = DarkGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bình luận", color = DarkGray)
                }
            }
        }
    }
}

@Composable
private fun FriendItem(
    friend: FriendResponse
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (!friend.avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = friend.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                val initials = if (!friend.firstName.isNullOrBlank() && !friend.lastName.isNullOrBlank()) {
                    "${friend.lastName.first()}${friend.firstName.first()}".uppercase()
                } else {
                    friend.username.take(2).uppercase()
                }
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.fullName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "@${friend.username}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

// Helper functions
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

private fun formatDateTime(dateTimeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)
        date?.let { outputFormat.format(it) } ?: dateTimeString
    } catch (e: Exception) {
        try {
            // Try another format
            val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat2.parse(dateTimeString)
            date?.let { outputFormat.format(it) } ?: dateTimeString
        } catch (e2: Exception) {
            dateTimeString
        }
    }
}
