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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.base.data.model.GroupResponse
import com.kma.base.data.model.GroupPrivacy

// Colors
private val PrimaryBlue = Color(0xFF1877F2)
private val LightGray = Color(0xFFF0F2F5)
private val DarkGray = Color(0xFF65676B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onBackClick: () -> Unit,
    onGroupClick: (String) -> Unit,
    onCreateGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Khám phá", "Nhóm của tôi")

    var allGroups by remember { mutableStateOf<List<GroupResponse>>(emptyList()) }
    var myGroups by remember { mutableStateOf<List<GroupResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    // TODO: Load groups from repository
    LaunchedEffect(selectedTab) {
        isLoading = true
        // Load groups
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nhóm") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateGroup,
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Group")
            }
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
                        text = { Text(title) }
                    )
                }
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm nhóm...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val groups = if (selectedTab == 0) allGroups else myGroups
                val filteredGroups = groups.filter {
                    it.displayName.contains(searchQuery, ignoreCase = true)
                }

                if (filteredGroups.isEmpty()) {
                    EmptyGroupsState(isMyGroups = selectedTab == 1)
                } else {
                    LazyColumn {
                        items(filteredGroups) { group ->
                            GroupItem(
                                group = group,
                                onClick = { onGroupClick(group.groupId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupItem(
    group: GroupResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Group icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Privacy badge
                    Surface(
                        color = if (group.isPublic) LightGray else PrimaryBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (group.isPublic) Icons.Default.Star else Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = DarkGray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (group.isPublic) "Công khai" else "Riêng tư",
                                fontSize = 10.sp,
                                color = DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Description
                if (!group.description.isNullOrBlank()) {
                    Text(
                        text = group.description,
                        color = DarkGray,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${group.memberCount} thành viên",
                            fontSize = 12.sp,
                            color = DarkGray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${group.postCount} bài viết",
                            fontSize = 12.sp,
                            color = DarkGray
                        )
                    }
                }
            }

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = DarkGray
            )
        }
    }
}

@Composable
private fun EmptyGroupsState(
    isMyGroups: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isMyGroups) "Bạn chưa tham gia nhóm nào" else "Không tìm thấy nhóm",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isMyGroups) "Khám phá và tham gia các nhóm mới" else "Thử tìm kiếm với từ khóa khác",
                color = DarkGray,
                fontSize = 14.sp
            )
        }
    }
}

// ============================================
// CREATE GROUP SCREEN
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBackClick: () -> Unit,
    onGroupCreated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo nhóm mới") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // TODO: Create group via API
                            if (groupName.isBlank()) {
                                errorMessage = "Vui lòng nhập tên nhóm"
                                return@TextButton
                            }
                            isLoading = true
                            // Call API and navigate
                        },
                        enabled = !isLoading && groupName.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = PrimaryBlue
                            )
                        } else {
                            Text("Tạo", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Group name
            OutlinedTextField(
                value = groupName,
                onValueChange = { 
                    groupName = it
                    errorMessage = null
                },
                label = { Text("Tên nhóm *") },
                placeholder = { Text("Nhập tên nhóm") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                placeholder = { Text("Mô tả về nhóm của bạn") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy
            Text(
                text = "Quyền riêng tư",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Public option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isPublic = true },
                colors = CardDefaults.cardColors(
                    containerColor = if (isPublic) PrimaryBlue.copy(alpha = 0.1f) else Color.White
                ),
                border = if (isPublic) CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PrimaryBlue)
                ) else null
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isPublic) PrimaryBlue else DarkGray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Công khai",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Mọi người đều có thể tìm thấy và xem nhóm",
                            fontSize = 13.sp,
                            color = DarkGray
                        )
                    }
                    RadioButton(
                        selected = isPublic,
                        onClick = { isPublic = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Private option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isPublic = false },
                colors = CardDefaults.cardColors(
                    containerColor = if (!isPublic) PrimaryBlue.copy(alpha = 0.1f) else Color.White
                ),
                border = if (!isPublic) CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PrimaryBlue)
                ) else null
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (!isPublic) PrimaryBlue else DarkGray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Riêng tư",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Chỉ thành viên mới có thể xem nội dung",
                            fontSize = 13.sp,
                            color = DarkGray
                        )
                    }
                    RadioButton(
                        selected = !isPublic,
                        onClick = { isPublic = false }
                    )
                }
            }
        }
    }
}
