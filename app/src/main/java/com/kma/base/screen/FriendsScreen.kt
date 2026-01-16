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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kma.base.data.model.FriendInfo
import com.kma.base.data.model.FriendRequest

// Colors
private val PrimaryBlue = Color(0xFF1877F2)
private val LightGray = Color(0xFFF0F2F5)
private val DarkGray = Color(0xFF65676B)
private val GreenAccent = Color(0xFF4CAF50)
private val RedAccent = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onBackClick: () -> Unit,
    onFriendClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Bạn bè", "Lời mời", "Gợi ý")

    var friends by remember { mutableStateOf<List<FriendInfo>>(emptyList()) }
    var requests by remember { mutableStateOf<List<FriendRequest>>(emptyList()) }
    var suggestions by remember { mutableStateOf<List<FriendInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // TODO: Load data from repository
    LaunchedEffect(selectedTab) {
        isLoading = true
        // Load based on selected tab
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bạn bè") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search friends */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(title)
                                if (index == 1 && requests.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge { Text("${requests.size}") }
                                }
                            }
                        }
                    )
                }
            }

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    0 -> FriendsList(friends = friends, onFriendClick = onFriendClick)
                    1 -> FriendRequestsList(
                        requests = requests,
                        onAccept = { /* Accept request */ },
                        onReject = { /* Reject request */ }
                    )
                    2 -> FriendSuggestionsList(
                        suggestions = suggestions,
                        onAddFriend = { /* Send friend request */ },
                        onProfileClick = onFriendClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendsList(
    friends: List<FriendInfo>,
    onFriendClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (friends.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Person,
            title = "Chưa có bạn bè",
            subtitle = "Tìm kiếm và kết bạn với mọi người"
        )
    } else {
        LazyColumn(modifier = modifier) {
            items(friends) { friend ->
                FriendItem(
                    friend = friend,
                    onClick = { onFriendClick(friend.userId) }
                )
            }
        }
    }
}

@Composable
private fun FriendRequestsList(
    requests: List<FriendRequest>,
    onAccept: (FriendRequest) -> Unit,
    onReject: (FriendRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    if (requests.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Add,
            title = "Không có lời mời kết bạn",
            subtitle = "Các lời mời mới sẽ hiển thị ở đây"
        )
    } else {
        LazyColumn(modifier = modifier) {
            items(requests) { request ->
                FriendRequestItem(
                    request = request,
                    onAccept = { onAccept(request) },
                    onReject = { onReject(request) }
                )
            }
        }
    }
}

@Composable
private fun FriendSuggestionsList(
    suggestions: List<FriendInfo>,
    onAddFriend: (FriendInfo) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Search,
            title = "Không có gợi ý",
            subtitle = "Chúng tôi sẽ gợi ý bạn bè khi có thể"
        )
    } else {
        LazyColumn(modifier = modifier) {
            items(suggestions) { suggestion ->
                SuggestionItem(
                    friend = suggestion,
                    onAddFriend = { onAddFriend(suggestion) },
                    onClick = { onProfileClick(suggestion.userId) }
                )
            }
        }
    }
}

@Composable
private fun FriendItem(
    friend: FriendInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            if (friend.avatarUrl != null) {
                AsyncImage(
                    model = friend.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = friend.fullName.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.fullName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            if (friend.mutualFriendsCount > 0) {
                Text(
                    text = "${friend.mutualFriendsCount} bạn chung",
                    color = DarkGray,
                    fontSize = 14.sp
                )
            }
        }

        // Chat button
        IconButton(onClick = { /* Start chat */ }) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Chat",
                tint = PrimaryBlue
            )
        }
    }
}

@Composable
private fun FriendRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            if (request.fromUserAvatar != null) {
                AsyncImage(
                    model = request.fromUserAvatar,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = request.fromUserName?.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = request.fromUserName ?: "Unknown",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Accept button
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Chấp nhận")
                }

                // Reject button
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Từ chối")
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    friend: FriendInfo,
    onAddFriend: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            if (friend.avatarUrl != null) {
                AsyncImage(
                    model = friend.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = friend.fullName.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.fullName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            if (friend.mutualFriendsCount > 0) {
                Text(
                    text = "${friend.mutualFriendsCount} bạn chung",
                    color = DarkGray,
                    fontSize = 14.sp
                )
            }
        }

        // Add friend button
        Button(
            onClick = onAddFriend,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Thêm")
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = DarkGray,
                fontSize = 14.sp
            )
        }
    }
}
