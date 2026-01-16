package com.kma.base.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kma.base.R
import com.kma.base.viewmodel.ChangePasswordViewModel
import com.kma.base.viewmodel.ChangePasswordUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChangePasswordViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    
    // State for password visibility
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // State for form fields
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Handle success navigation
    LaunchedEffect(uiState) {
        if (uiState is ChangePasswordUiState.Success) {
            kotlinx.coroutines.delay(1500)
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Đổi mật khẩu",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon header
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Thay đổi mật khẩu đăng nhập",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Success message
                AnimatedVisibility(
                    visible = uiState is ChangePasswordUiState.Success,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 20.sp,
                                color = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = (uiState as? ChangePasswordUiState.Success)?.message 
                                    ?: "Đổi mật khẩu thành công!",
                                color = Color(0xFF4CAF50),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                // Error message
                AnimatedVisibility(
                    visible = uiState is ChangePasswordUiState.Error,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = (uiState as? ChangePasswordUiState.Error)?.message 
                                    ?: "Có lỗi xảy ra",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Password Form
                AnimatedVisibility(
                    visible = uiState !is ChangePasswordUiState.Success,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Old Password Field
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { Text("Mật khẩu hiện tại") },
                            placeholder = { Text("Nhập mật khẩu hiện tại") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (oldPasswordVisible) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (oldPasswordVisible) 
                                                R.drawable.baseline_visibility_24 
                                            else 
                                                R.drawable.baseline_visibility_off_24
                                        ),
                                        contentDescription = if (oldPasswordVisible) "Ẩn" else "Hiện"
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = uiState !is ChangePasswordUiState.Loading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // New Password Field
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Mật khẩu mới") },
                            placeholder = { Text("Nhập mật khẩu mới (tối thiểu 6 ký tự)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (newPasswordVisible) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (newPasswordVisible) 
                                                R.drawable.baseline_visibility_24 
                                            else 
                                                R.drawable.baseline_visibility_off_24
                                        ),
                                        contentDescription = if (newPasswordVisible) "Ẩn" else "Hiện"
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = uiState !is ChangePasswordUiState.Loading,
                            supportingText = {
                                if (newPassword.isNotEmpty() && newPassword.length < 6) {
                                    Text(
                                        text = "Mật khẩu phải có ít nhất 6 ký tự",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Confirm Password Field
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Xác nhận mật khẩu mới") },
                            placeholder = { Text("Nhập lại mật khẩu mới") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (confirmPasswordVisible) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (confirmPasswordVisible) 
                                                R.drawable.baseline_visibility_24 
                                            else 
                                                R.drawable.baseline_visibility_off_24
                                        ),
                                        contentDescription = if (confirmPasswordVisible) "Ẩn" else "Hiện"
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (isFormValid(oldPassword, newPassword, confirmPassword)) {
                                        viewModel.changePassword(oldPassword, newPassword)
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = uiState !is ChangePasswordUiState.Loading,
                            isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                            supportingText = {
                                if (confirmPassword.isNotEmpty()) {
                                    if (newPassword == confirmPassword) {
                                        Text(
                                            text = "✓ Mật khẩu khớp",
                                            color = Color(0xFF4CAF50)
                                        )
                                    } else {
                                        Text(
                                            text = "Mật khẩu không khớp",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Submit Button
                        Button(
                            onClick = { viewModel.changePassword(oldPassword, newPassword) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = isFormValid(oldPassword, newPassword, confirmPassword) && 
                                     uiState !is ChangePasswordUiState.Loading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState is ChangePasswordUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Đổi mật khẩu",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isFormValid(
    oldPassword: String,
    newPassword: String,
    confirmPassword: String
): Boolean {
    return oldPassword.isNotBlank() &&
           newPassword.length >= 6 &&
           newPassword == confirmPassword &&
           oldPassword != newPassword
}
