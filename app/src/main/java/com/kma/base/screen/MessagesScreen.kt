package com.kma.base.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kma.base.R
import com.kma.base.data.model.ConversationResponse
import com.kma.base.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
        modifier: Modifier = Modifier,
        onChatClick: (conversationId: String, conversationName: String) -> Unit = { _, _ -> },
        chatViewModel: ChatViewModel = viewModel()
) {
    val conversationsState by chatViewModel.conversationsState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Load conversations on screen open
    LaunchedEffect(Unit) {
        chatViewModel.loadConversations()
        chatViewModel.connectWebSocket()
    }

    // Get current user ID for display
    val currentUserId = chatViewModel.getCurrentUserId() ?: ""

    // Filter conversations by search query
    val filteredConversations =
            remember(conversationsState.conversations, searchQuery) {
                if (searchQuery.isBlank()) {
                    conversationsState.conversations
                } else {
                    conversationsState.conversations.filter { conv ->
                        conv.getDisplayName(currentUserId).contains(searchQuery, ignoreCase = true)
                    }
                }
            }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(text = "Tin nhắn", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        titleContentColor = MaterialTheme.colorScheme.onSurface
                                )
                )
            }
    ) { paddingValues ->
        Column(
                modifier =
                        modifier.fillMaxSize()
                                .padding(paddingValues)
                                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content
            when {
                conversationsState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                conversationsState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                    text = conversationsState.error ?: "Error",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { chatViewModel.loadConversations() }) {
                                Text("Thử lại")
                            }
                        }
                    }
                }
                filteredConversations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                                text =
                                        if (searchQuery.isNotBlank()) {
                                            "Không tìm thấy cuộc trò chuyện"
                                        } else {
                                            "Chưa có tin nhắn nào\nHãy bắt đầu trò chuyện!"
                                        },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(items = filteredConversations, key = { it.id }) { conversation ->
                            ConversationItem(
                                    conversation = conversation,
                                    currentUserId = currentUserId,
                                    onClick = {
                                        chatViewModel.setCurrentConversation(conversation)
                                        onChatClick(
                                                conversation.id,
                                                conversation.getDisplayName(currentUserId)
                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = modifier,
            placeholder = { Text("Tìm kiếm tin nhắn...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            shape = RoundedCornerShape(24.dp),
            colors =
                    OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor =
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
            singleLine = true
    )
}

@Composable
fun ConversationItem(
        conversation: ConversationResponse,
        currentUserId: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    val displayName = conversation.getDisplayName(currentUserId)
    val unreadCount = conversation.getUnreadCount(currentUserId)
    val avatarUrl = conversation.getOtherParticipantAvatar(currentUserId)
    val avatarColor =
            remember(conversation.id) {
                val colors =
                        listOf(
                                Color(0xFF6200EE),
                                Color(0xFF03DAC6),
                                Color(0xFFFF6B6B),
                                Color(0xFF4ECDC4),
                                Color(0xFFFFBE0B),
                                Color(0xFF845EC2)
                        )
                colors[
                        conversation.id.hashCode().mod(colors.size).let {
                            if (it < 0) it + colors.size else it
                        }]
            }

    Row(
            modifier =
                    modifier.fillMaxWidth()
                            .clickable(onClick = onClick)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(avatarColor),
                contentAlignment = Alignment.Center
        ) {
            if (conversation.isGroup) {
                Icon(
                        painter = painterResource(id = R.drawable.baseline_group_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                )
            } else if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Chat Info
        Column(modifier = Modifier.weight(1f)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                        text = formatConversationTime(conversation.lastMessageAt),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = conversation.lastMessage ?: "Bắt đầu trò chuyện",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                )

                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                            modifier =
                                    Modifier.size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun formatConversationTime(isoDate: String?): String {
    if (isoDate.isNullOrBlank()) return ""

    return try {
        val formats =
                listOf(
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                )

        var date: Date? = null
        for (format in formats) {
            try {
                format.timeZone = TimeZone.getTimeZone("UTC")
                date = format.parse(isoDate)
                if (date != null) break
            } catch (e: Exception) {
                continue
            }
        }

        if (date == null) return ""

        val now = System.currentTimeMillis()
        val diff = now - date.time

        when {
            diff < 60000 -> "Vừa xong"
            diff < 3600000 -> "${diff / 60000}p"
            diff < 86400000 -> "${diff / 3600000}h"
            diff < 604800000 -> "${diff / 86400000}d"
            else -> {
                val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
                sdf.format(date)
            }
        }
    } catch (e: Exception) {
        ""
    }
}
