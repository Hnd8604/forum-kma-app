package com.kma.base.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kma.base.data.model.CommentResponse
import com.kma.base.data.model.PostWithInteractionResponse
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kma.base.viewmodel.PostDetailViewModel

// Colors
private val PrimaryBlue = Color(0xFF1877F2)
private val LightGray = Color(0xFFF0F2F5)
private val DarkGray = Color(0xFF65676B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var commentText by remember { mutableStateOf("") }

    // Load data
    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài viết") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        },
        bottomBar = {
            // Comment input
            CommentInputBar(
                value = commentText,
                onValueChange = { commentText = it },
                onSend = {
                    viewModel.sendComment(postId, commentText)
                    commentText = ""
                },
                isLoading = uiState.isSendingComment
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
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
                    Button(onClick = { viewModel.loadPost(postId) }) {
                        Text("Thử lại")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Post content
                item {
                    PostDetailContent(
                        post = uiState.post,
                        isLiked = uiState.userReaction != null,
                        onLikeClick = { viewModel.toggleReaction(postId) },
                        onCommentClick = { /* Focus comment input */ }
                    )
                }

                // Divider
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 8.dp,
                        color = LightGray
                    )
                }

                // Comments header
                item {
                    Text(
                        text = "Bình luận (${uiState.comments.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Loading comments
                if (uiState.isLoadingComments) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                // Comments list
                items(uiState.comments) { comment ->
                    CommentItem(
                        comment = comment,
                        onReplyClick = { /* Reply to comment */ },
                        onLikeClick = { /* Like comment */ }
                    )
                }

                // Empty state
                if (uiState.comments.isEmpty() && !uiState.isLoadingComments) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có bình luận nào.\nHãy là người đầu tiên bình luận!",
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

@Composable
private fun PostDetailContent(
    post: PostWithInteractionResponse?,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Author info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (post?.authorName?.firstOrNull() ?: "?").toString().uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post?.authorName ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post?.createdAt?.let { formatTimeAgo(it) } ?: "",
                        color = DarkGray,
                        fontSize = 13.sp
                    )
                    if (post?.groupName != null) {
                        Text(
                            text = " · ",
                            color = DarkGray
                        )
                        Text(
                            text = post.groupName,
                            color = PrimaryBlue,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        if (!post?.title.isNullOrBlank()) {
            Text(
                text = post?.title ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Content
        Text(
            text = post?.content ?: "",
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        // Images
        if (!post?.resourceUrls.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            post?.resourceUrls?.forEach { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${post?.reactionCount ?: 0} lượt thích",
                color = DarkGray,
                fontSize = 14.sp
            )
            Text(
                text = "${post?.commentCount ?: 0} bình luận",
                color = DarkGray,
                fontSize = 14.sp
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Action buttons - only Like and Comment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Like button
            TextButton(
                onClick = onLikeClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isLiked) PrimaryBlue else DarkGray
                )
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isLiked) "Đã thích" else "Thích")
            }

            // Comment button
            TextButton(
                onClick = onCommentClick,
                colors = ButtonDefaults.textButtonColors(contentColor = DarkGray)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Comment",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bình luận")
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: CommentResponse,
    onReplyClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            if (comment.authorAvatarUrl != null) {
                AsyncImage(
                    model = comment.authorAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = (comment.authorName?.firstOrNull() ?: "?").toString().uppercase(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Comment bubble
            Surface(
                color = LightGray,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = comment.authorName ?: "Unknown",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = comment.content,
                        fontSize = 14.sp
                    )
                }
            }

            // Actions row
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = formatTimeAgo(comment.createdAt),
                    color = DarkGray,
                    fontSize = 12.sp
                )
                Text(
                    text = "Thích",
                    color = if (comment.myReaction != null) PrimaryBlue else DarkGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onLikeClick)
                )
                Text(
                    text = "Trả lời",
                    color = DarkGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onReplyClick)
                )
                if (comment.reactionCount > 0) {
                    Text(
                        text = "👍 ${comment.reactionCount}",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Input field
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Viết bình luận...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = LightGray,
                    focusedBorderColor = PrimaryBlue
                ),
                maxLines = 3,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send button
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (value.isNotBlank()) PrimaryBlue else DarkGray
                    )
                }
            }
        }
    }
}

private fun formatTimeAgo(isoDate: String): String {
    return try {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        val date = format.parse(isoDate) ?: return isoDate
        val now = java.util.Date()
        val diff = now.time - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            seconds < 60 -> "Vừa xong"
            minutes < 60 -> "${minutes}p"
            hours < 24 -> "${hours}h"
            days < 7 -> "${days}d"
            else -> java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        isoDate
    }
}
