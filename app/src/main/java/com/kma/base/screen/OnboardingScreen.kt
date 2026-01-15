package com.kma.base.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.base.R
import com.kma.base.model.AppTheme
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = "Chào mừng",
            description = "Khám phá ứng dụng với giao diện hiện đại và dễ sử dụng",
            icon = Icons.Default.Home
        ),
        OnboardingPage(
            title = "Quản lý Profile",
            description = "Cá nhân hóa thông tin của bạn một cách dễ dàng",
            icon = Icons.Default.Person
        ),
        OnboardingPage(
            title = "Cài đặt linh hoạt",
            description = "Tùy chỉnh ứng dụng theo cách bạn muốn",
            icon = Icons.Default.Settings
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size + 1 })
    val scope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Skip button
            if (pagerState.currentPage < pages.size) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pages.size)
                        }
                    }) {
                        Text("Bỏ qua")
                    }
                }
            }

            // Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                if (page < pages.size) {
                    OnboardingPageContent(pages[page])
                } else {
                    ThemeSelectionPage(
                        selectedTheme = currentTheme,
                        onThemeSelected = onThemeSelected
                    )
                }
            }

            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size + 1) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (pagerState.currentPage == index) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < pages.size) "Tiếp theo" else "Bắt đầu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = page.title,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
    }
}

@Composable
fun ThemeSelectionPage(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chọn giao diện",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bạn có thể thay đổi sau trong Cài đặt",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // System Mode Option
        ThemeOptionCard(
            title = "Theo hệ thống",
            description = "Tự động theo điện thoại",
            iconRes = R.drawable.brightness_auto_24px,
            currentTheme = selectedTheme,
            targetTheme = AppTheme.SYSTEM,
            onClick = { onThemeSelected(AppTheme.SYSTEM) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Light Mode Option
        ThemeOptionCard(
            title = "Light Mode",
            description = "Giao diện sáng",
            iconRes = R.drawable.brightness_7_24px,
            currentTheme = selectedTheme,
            targetTheme = AppTheme.LIGHT,
            onClick = { onThemeSelected(AppTheme.LIGHT) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dark Mode Option
        ThemeOptionCard(
            title = "Dark Mode",
            description = "Giao diện tối",
            iconRes = R.drawable.brightness_4_24px,
            currentTheme = selectedTheme,
            targetTheme = AppTheme.DARK,
            onClick = { onThemeSelected(AppTheme.DARK) }
        )
    }
}
