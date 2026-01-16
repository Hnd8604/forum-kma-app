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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kma.base.data.model.PostWithInteractionResponse
import com.kma.base.viewmodel.HomeViewModel

// Colors
private val PrimaryBlue = Color(0xFF1877F2)
private val LightGray = Color(0xFFF0F2F5)
private val DarkGray = Color(0xFF65676B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onPostClick: (String) -> Unit = {},
    onCreatePostClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onFriendsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadPosts()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Forum KMA",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    ) 
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = onFriendsClick) {
                        Icon(Icons.Default.Person, contentDescription = "Friends")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePostClick,
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.posts.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null && uiState.posts.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "Đã xảy ra lỗi",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPosts() }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Create post card
                    item {
                        CreatePostCard(onClick = onCreatePostClick)
                    }
                    
                    // Posts - filter out any posts with null id
                    val validPosts = uiState.posts.filter { it.id != null }
                    items(
                        items = validPosts,
                        key = { it.id!! }  // Safe because we filtered above
                    ) { post ->
                        PostItem(
                            post = post,
                            onClick = { post.id?.let { onPostClick(it) } },
                            onLikeClick = { 
                                post.id?.let { postId ->
                                    viewModel.toggleLike(postId, post.userReaction != null) 
                                }
                            },
                            onCommentClick = { post.id?.let { onPostClick(it) } }
                        )
                    }
                    
                    // Loading more indicator
                    if (uiState.isLoading && uiState.posts.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    
                    // Empty state
                    if (uiState.posts.isEmpty() && !uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Create,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Chưa có bài viết nào",
                                        color = DarkGray,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Hãy tạo bài viết đầu tiên!",
                                        color = DarkGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePostCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Placeholder text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LightGray)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    "Bạn đang nghĩ gì?",
                    color = DarkGray,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Image icon
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Add Image",
                    tint = Color(0xFF45BD62)
                )
            }
        }
    }
}

@Composable
private fun PostItem(
    post: PostWithInteractionResponse,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                    Text(
                        text = (post.authorName?.firstOrNull() ?: "U").toString().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
                            text = formatTimeAgo(post.createdAt),
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
            if (!post.title.isNullOrBlank()) {
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
            
            // Images
            if (!post.resourceUrls.isNullOrEmpty() && post.type == "IMAGE") {
                Spacer(modifier = Modifier.height(8.dp))
                // Show first image
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
                        imageVector = if (post.userReaction != null) Icons.Filled.ThumbUp else Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = if (post.userReaction != null) PrimaryBlue else DarkGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Thích",
                        color = if (post.userReaction != null) PrimaryBlue else DarkGray
                    )
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

// Helper function to format time
private fun formatTimeAgo(dateString: String): String {
    return try {
        // Simple format - just show the time portion
        if (dateString.contains("T")) {
            val time = dateString.substringAfter("T").substringBefore(".")
            val parts = time.split(":")
            if (parts.size >= 2) {
                "${parts[0]}:${parts[1]}"
            } else {
                dateString
            }
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}
