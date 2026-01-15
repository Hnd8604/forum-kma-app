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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.base.R
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val avatarColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample data - sẽ thay thế bằng data từ API
    val sampleChats = remember {
        listOf(
            ChatMessage(
                id = "1",
                name = "Nhóm Lập trình Android",
                lastMessage = "Phong: Mọi người đã làm bài tập chưa?",
                timestamp = System.currentTimeMillis() - 300000,
                unreadCount = 3,
                isGroup = true,
                avatarColor = Color(0xFF6200EE)
            ),
            ChatMessage(
                id = "2",
                name = "Nguyễn Văn A",
                lastMessage = "Ok bạn, mai mình gặp nhé!",
                timestamp = System.currentTimeMillis() - 3600000,
                unreadCount = 0,
                isGroup = false,
                avatarColor = Color(0xFF03DAC6)
            ),
            ChatMessage(
                id = "3",
                name = "Nhóm TTCS K67",
                lastMessage = "Hôm nay có buổi họp lúc 2h chiều",
                timestamp = System.currentTimeMillis() - 7200000,
                unreadCount = 5,
                isGroup = true,
                avatarColor = Color(0xFFFF6B6B)
            ),
            ChatMessage(
                id = "4",
                name = "Trần Thị B",
                lastMessage = "Cảm ơn bạn nhiều!",
                timestamp = System.currentTimeMillis() - 86400000,
                unreadCount = 0,
                isGroup = false,
                avatarColor = Color(0xFF4ECDC4)
            ),
            ChatMessage(
                id = "5",
                name = "Nhóm Học tập K67",
                lastMessage = "Ai có tài liệu môn CSDL không?",
                timestamp = System.currentTimeMillis() - 172800000,
                unreadCount = 1,
                isGroup = true,
                avatarColor = Color(0xFFFFBE0B)
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tin nhắn",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Chat List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(sampleChats) { chat ->
                    ChatItem(
                        chat = chat,
                        onClick = {
                            // TODO: Navigate to chat detail
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text("Tìm kiếm tin nhắn...")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        singleLine = true
    )
}

@Composable
fun ChatItem(
    chat: ChatMessage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(chat.avatarColor),
            contentAlignment = Alignment.Center
        ) {
            if (chat.isGroup) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_group_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
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
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = formatTimestamp(chat.timestamp),
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
                    text = chat.lastMessage,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (chat.unreadCount > 9) "9+" else chat.unreadCount.toString(),
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

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Vừa xong"
        diff < 3600000 -> "${diff / 60000} phút"
        diff < 86400000 -> "${diff / 3600000} giờ"
        diff < 604800000 -> "${diff / 86400000} ngày"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
