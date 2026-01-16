package com.kma.base.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.kma.base.data.model.GroupResponse

// Colors
private val PrimaryBlue = Color(0xFF1877F2)
private val LightGray = Color(0xFFF0F2F5)
private val DarkGray = Color(0xFF65676B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onBackClick: () -> Unit,
    onPostCreated: () -> Unit,
    selectedGroupId: String? = null,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<GroupResponse?>(null) }
    var showGroupPicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            // Limit to 10 images
            selectedImages = (selectedImages + uris).take(10)
        }
    }

    // Available groups for selection
    var availableGroups by remember { mutableStateOf<List<GroupResponse>>(emptyList()) }

    // TODO: Load groups from repository
    LaunchedEffect(Unit) {
        // Load my groups
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo bài viết") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            // Validate
                            if (content.isBlank()) {
                                errorMessage = "Vui lòng nhập nội dung bài viết"
                                return@Button
                            }
                            isLoading = true
                            // TODO: Create post via API
                            // Upload images first, then create post with URLs
                        },
                        enabled = !isLoading && content.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Đăng")
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
                .verticalScroll(rememberScrollState())
        ) {
            // Author info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Bạn",  // TODO: Get current user name
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    // Group selector
                    Surface(
                        onClick = { showGroupPicker = true },
                        shape = RoundedCornerShape(4.dp),
                        color = LightGray
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (selectedGroup == null) Icons.Default.Star else Icons.Default.Face,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = DarkGray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedGroup?.displayName ?: "Chọn nhóm",
                                fontSize = 12.sp,
                                color = DarkGray
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = DarkGray
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { 
                    Text(
                        "Tiêu đề (không bắt buộc)",
                        color = DarkGray
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                singleLine = true
            )

            // Content input
            OutlinedTextField(
                value = content,
                onValueChange = { 
                    content = it 
                    errorMessage = null
                },
                placeholder = { 
                    Text(
                        "Bạn đang nghĩ gì?",
                        color = DarkGray
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
                    .padding(horizontal = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                isError = errorMessage != null,
                supportingText = errorMessage?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                }
            )

            // Selected images
            if (selectedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedImages) { uri ->
                        Box {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            
                            // Remove button
                            IconButton(
                                onClick = { 
                                    selectedImages = selectedImages.filter { it != uri }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Add more button
                    if (selectedImages.size < 10) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 2.dp,
                                        color = LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = DarkGray,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Thêm ảnh",
                                        fontSize = 12.sp,
                                        color = DarkGray
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "${selectedImages.size}/10 ảnh",
                    fontSize = 12.sp,
                    color = DarkGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            HorizontalDivider()
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Add image
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Add Image",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Ảnh",
                        fontSize = 12.sp,
                        color = DarkGray
                    )
                }

                // Add video (placeholder)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { /* TODO */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Add Video",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Video",
                        fontSize = 12.sp,
                        color = DarkGray
                    )
                }

                // Add document (placeholder)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { /* TODO */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add File",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Tài liệu",
                        fontSize = 12.sp,
                        color = DarkGray
                    )
                }

                // Tag people (placeholder)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { /* TODO */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Tag People",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Tag",
                        fontSize = 12.sp,
                        color = DarkGray
                    )
                }
            }
        }
    }

    // Group picker dialog
    if (showGroupPicker) {
        GroupPickerDialog(
            groups = availableGroups,
            selectedGroup = selectedGroup,
            onSelect = { group ->
                selectedGroup = group
                showGroupPicker = false
            },
            onDismiss = { showGroupPicker = false }
        )
    }
}

@Composable
private fun GroupPickerDialog(
    groups: List<GroupResponse>,
    selectedGroup: GroupResponse?,
    onSelect: (GroupResponse?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn nhóm") },
        text = {
            Column {
                // Option: No group
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(null) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (selectedGroup == null) PrimaryBlue else DarkGray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Không chọn nhóm",
                        fontWeight = if (selectedGroup == null) FontWeight.Bold else FontWeight.Normal
                    )
                }

                HorizontalDivider()

                // Groups list
                groups.forEach { group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(group) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = if (selectedGroup?.groupId == group.groupId) PrimaryBlue else DarkGray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = group.displayName,
                                fontWeight = if (selectedGroup?.groupId == group.groupId) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${group.memberCount} thành viên",
                                fontSize = 12.sp,
                                color = DarkGray
                            )
                        }
                    }
                }

                if (groups.isEmpty()) {
                    Text(
                        text = "Bạn chưa tham gia nhóm nào",
                        color = DarkGray,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}
