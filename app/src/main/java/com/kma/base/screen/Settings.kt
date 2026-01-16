package com.kma.base.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.base.R
import com.kma.base.model.AppTheme

@Composable
@Preview
fun SettingsScreen(
    currentTheme: AppTheme = AppTheme.SYSTEM,
    onThemeSelected: (AppTheme) -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Cài đặt",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // ===== Giao diện Section =====
            SettingsSectionHeader(title = "Giao diện")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // System Mode Card
            ThemeOptionCard(
                title = "Theo hệ thống",
                description = "Tự động theo điện thoại",
                iconRes = R.drawable.brightness_auto_24px,
                currentTheme = currentTheme,
                targetTheme = AppTheme.SYSTEM,
                onClick = { onThemeSelected(AppTheme.SYSTEM) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Light Mode Card
            ThemeOptionCard(
                title = "Sáng",
                description = "Giao diện sáng",
                iconRes = R.drawable.brightness_7_24px,
                currentTheme = currentTheme,
                targetTheme = AppTheme.LIGHT,
                onClick = { onThemeSelected(AppTheme.LIGHT) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Dark Mode Card
            ThemeOptionCard(
                title = "Tối",
                description = "Giao diện tối",
                iconRes = R.drawable.brightness_4_24px,
                currentTheme = currentTheme,
                targetTheme = AppTheme.DARK,
                onClick = { onThemeSelected(AppTheme.DARK) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // ===== Thông báo Section =====
            SettingsSectionHeader(title = "Thông báo")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsMenuItem(
                icon = Icons.Default.Notifications,
                title = "Cài đặt thông báo",
                description = "Quản lý thông báo đẩy",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // ===== Bảo mật Section =====
            SettingsSectionHeader(title = "Bảo mật")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsMenuItem(
                icon = Icons.Default.Lock,
                title = "Đổi mật khẩu",
                description = "Thay đổi mật khẩu đăng nhập",
                onClick = onNavigateToChangePassword
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // ===== Thông tin Section =====
            SettingsSectionHeader(title = "Thông tin")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsMenuItem(
                icon = Icons.Default.Info,
                title = "Về ứng dụng",
                description = "Phiên bản 1.0.0",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            
            // ===== Logout Button =====
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
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
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ThemeOptionCard(
    title: String,
    description: String,
    iconRes: Int,
    currentTheme: AppTheme,
    targetTheme: AppTheme,
    onClick: () -> Unit
) {
    val isSelected = currentTheme == targetTheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(32.dp),
                    tint = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}