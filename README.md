# Android Base Project - Hướng Dẫn Tùy Chỉnh

## Mục Lục
- [Giới Thiệu](#giới-thiệu)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Tùy Chỉnh Logo và Splash Screen](#tùy-chỉnh-logo-và-splash-screen)
- [Tùy Chỉnh Theme và Màu Sắc](#tùy-chỉnh-theme-và-màu-sắc)
- [Tùy Chỉnh Navigation](#tùy-chỉnh-navigation)
- [Tùy Chỉnh Onboarding](#tùy-chỉnh-onboarding)
- [Thêm Màn Hình Mới](#thêm-màn-hình-mới)
- [Build và Deploy](#build-và-deploy)

---

## Giới Thiệu

Đây là project Android base sử dụng:
- **Jetpack Compose** - UI hiện đại
- **Material Design 3** - Design system
- **Navigation Compose** - Điều hướng
- **Splash Screen API** - Màn hình khởi động
- **SharedPreferences** - Lưu trữ cài đặt

Dự án đã có sẵn:
- ✅ Splash Screen với animation
- ✅ Onboarding Screen (3 trang)
- ✅ Bottom Navigation (Home, Profile, Settings)
- ✅ Theme System (Light/Dark/Follow System)
- ✅ Responsive UI

---

## Cấu Trúc Dự Án

```
app/src/main/
├── java/com/kma/base/
│   ├── MainActivity.kt              # Activity chính
│   ├── MainScreen.kt                # Main screen với bottom nav
│   ├── BottomNavGraph.kt            # Navigation graph
│   ├── NavigationItem.kt            # Định nghĩa navigation items
│   ├── screen/
│   │   ├── SplashScreen.kt         # Splash screen (không dùng nữa)
│   │   ├── OnboardingScreen.kt     # Onboarding
│   │   ├── HomeScreen.kt           # Home
│   │   ├── ProfileScreen.kt        # Profile
│   │   └── Settings.kt             # Settings
│   └── ui/theme/
│       ├── Color.kt                # Định nghĩa màu
│       ├── Theme.kt                # Theme setup
│       └── Type.kt                 # Typography
├── res/
│   ├── drawable/                   # Icons và images
│   ├── drawable-night/             # Icons cho dark mode
│   ├── values/
│   │   ├── colors.xml             # Màu sắc
│   │   ├── strings.xml            # Text strings
│   │   └── themes.xml             # Light theme
│   └── values-night/
│       ├── colors.xml             # Màu dark mode
│       └── themes.xml             # Dark theme
└── AndroidManifest.xml            # App manifest
```

---

## Tùy Chỉnh Logo và Splash Screen

### 1. Thay đổi Logo App

**Vị trí file:**
- Logo chính: `app/src/main/res/drawable/applogo2.png`
- Logo dark mode: `app/src/main/res/drawable-night/applogo_night.png` (tùy chọn)

**Cách thay:**
1. Chuẩn bị logo (format PNG, nền trong suốt)
2. Thay thế file `applogo2.png`
3. Nếu muốn logo khác cho dark mode, thêm `applogo_night.png` vào `drawable-night/`

### 2. Điều chỉnh kích thước Logo trên Splash Screen

**File:** `app/src/main/res/drawable/splash_logo.xml`

```xml
<item
    android:drawable="@drawable/applogo2"
    android:width="200dp"        <!-- Thay đổi kích thước -->
    android:height="200dp"       <!-- Thay đổi kích thước -->
    android:gravity="center" />
```

### 3. Thay đổi màu nền Splash Screen

**File Light Mode:** `app/src/main/res/values/colors.xml`
```xml
<color name="splash_background">#FFFFFFFF</color> <!-- Màu trắng -->
```

**File Dark Mode:** `app/src/main/res/values-night/colors.xml`
```xml
<color name="splash_background">#FF000000</color> <!-- Màu đen -->
```

### 4. Tùy chỉnh Animation Splash

**File:** `app/src/main/java/com/kma/base/MainActivity.kt`

Tìm `splashScreen.setOnExitAnimationListener`:
```kotlin
slideUp.duration = 500L          // Thời gian animation (ms)
fadeOut.duration = 500L          // Thời gian fade out (ms)
```

**File:** `app/src/main/res/values/themes.xml`
```xml
<item name="windowSplashScreenAnimationDuration">1000</item> <!-- ms -->
```

---

## Tùy Chỉnh Theme và Màu Sắc

### 1. Thay đổi màu chính của app

**File:** `app/src/main/java/com/kma/base/ui/theme/Color.kt`

```kotlin
// Light Theme Colors
val md_theme_light_primary = Color(0xFF6200EE)      // Màu chính
val md_theme_light_secondary = Color(0xFF03DAC6)    // Màu phụ
val md_theme_light_background = Color(0xFFFFFFFF)   // Nền

// Dark Theme Colors
val md_theme_dark_primary = Color(0xFFBB86FC)
val md_theme_dark_secondary = Color(0xFF03DAC6)
val md_theme_dark_background = Color(0xFF121212)
```

### 2. Màu Navigation Bar

**File:** `app/src/main/java/com/kma/base/MainScreen.kt`

Tìm `NavigationBarItemDefaults`:
```kotlin
colors = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.White,
    unselectedIconColor = Color.Gray,
    indicatorColor = Color.Blue
)
```

---

## Tùy Chỉnh Navigation

### 1. Thêm/Xóa Tab Navigation

**File:** `app/src/main/java/com/kma/base/NavigationItem.kt`

```kotlin
sealed class NavigationItem(val route: String, val icon: Int, val title: String) {
    object Home : NavigationItem("home", R.drawable.home_24px, "Home")
    object Profile : NavigationItem("profile", R.drawable.person_24px, "Profile")
    object Settings : NavigationItem("settings", R.drawable.settings_24px, "Settings")
    
    // Thêm tab mới:
    // object NewTab : NavigationItem("newtab", R.drawable.icon_new, "New Tab")
}
```

**File:** `app/src/main/java/com/kma/base/MainScreen.kt`

Cập nhật danh sách screens:
```kotlin
val screens = listOf(
    NavigationItem.Home,
    NavigationItem.Profile,
    NavigationItem.Settings,
    // NavigationItem.NewTab  // Thêm vào đây
)
```

### 2. Thay đổi Icon Navigation

**Vị trí:** `app/src/main/res/drawable/`

Thêm icon mới (format XML hoặc PNG) và cập nhật trong `NavigationItem.kt`:
```kotlin
object Home : NavigationItem("home", R.drawable.icon_moi, "Home")
```

---

## Tùy Chỉnh Onboarding

### 1. Thay đổi nội dung Onboarding

**File:** `app/src/main/java/com/kma/base/screen/OnboardingScreen.kt`

Tìm `val pages = listOf(...)`:
```kotlin
val pages = listOf(
    OnboardingPage(
        title = "Tiêu đề mới",
        description = "Mô tả mới",
        icon = Icons.Default.Home  // Thay icon
    ),
    // Thêm trang mới hoặc xóa trang
)
```

### 2. Thay đổi màu Theme Selection

**File:** `app/src/main/java/com/kma/base/screen/OnboardingScreen.kt`

Tìm `ThemeOptionCard` và sửa `colors`:
```kotlin
colors = CardDefaults.cardColors(
    containerColor = if (isSelected) 
        MaterialTheme.colorScheme.primaryContainer 
    else 
        MaterialTheme.colorScheme.surfaceVariant
)
```

### 3. Tùy chỉnh các trang trong Onboarding

**File:** `app/src/main/java/com/kma/base/screen/OnboardingScreen.kt`

Tìm `val pages = listOf(...)` và sửa nội dung:
```kotlin
val pages = listOf(
    OnboardingPage(
        title = "Tiêu đề trang 1",
        description = "Mô tả trang 1",
        icon = Icons.Default.Home  // Thay icon
    ),
    OnboardingPage(
        title = "Tiêu đề trang 2",
        description = "Mô tả trang 2",
        icon = Icons.Default.Person
    ),
    OnboardingPage(
        title = "Tiêu đề trang 3",
        description = "Mô tả trang 3",
        icon = Icons.Default.Settings
    )
    // Có thể thêm hoặc xóa trang
)
```

**Lưu ý:** 
- Onboarding chỉ hiển thị lần đầu mở app
- Có thể bỏ qua bằng nút "Bỏ qua"
- Để test lại: Clear app data hoặc uninstall rồi cài lại

---

## Thêm Màn Hình Mới

### Bước 1: Tạo file Screen mới

**Vị trí:** `app/src/main/java/com/kma/base/screen/NewScreen.kt`

```kotlin
package com.kma.base.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "New Screen",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
```

### Bước 2: Thêm vào Navigation Graph

**File:** `app/src/main/java/com/kma/base/BottomNavGraph.kt`

```kotlin
import com.kma.base.screen.NewScreen

// Trong NavHost:
composable(route = NavigationItem.NewTab.route) {
    NewScreen()
}
```

### Bước 3: Thêm Navigation Item (nếu cần bottom nav)

Xem phần [Tùy Chỉnh Navigation](#tùy-chỉnh-navigation)

---

## Build và Deploy

### 1. Đổi tên App

**File:** `app/src/main/res/values/strings.xml`
```xml
<string name="app_name">Tên App Mới</string>
```

### 2. Đổi Package Name

**File:** `app/build.gradle.kts`
```kotlin
android {
    namespace = "com.yourcompany.yourapp"  // Đổi package name
    defaultConfig {
        applicationId = "com.yourcompany.yourapp"
    }
}
```

Sau đó refactor package trong Android Studio:
1. Right-click vào package `com.kma.base`
2. Refactor → Rename
3. Nhập package name mới

### 3. Build APK

```bash
./gradlew assembleDebug      # Build debug APK
./gradlew assembleRelease    # Build release APK
```

APK sẽ nằm ở: `app/build/outputs/apk/`

### 4. Tạo Keystore để ký app

```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

**File:** `app/build.gradle.kts`
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "password"
            keyAlias = "my-key-alias"
            keyPassword = "password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## Các File Quan Trọng Cần Chỉnh

### ✅ Bắt buộc phải sửa:
1. ✏️ **Logo**: `app/src/main/res/drawable/applogo2.png`
2. ✏️ **Tên app**: `app/src/main/res/values/strings.xml`
3. ✏️ **Package name**: `app/build.gradle.kts`
4. ✏️ **Màu sắc**: `app/src/main/java/com/kma/base/ui/theme/Color.kt`

### 🎨 Nên tùy chỉnh:
5. ✏️ **Onboarding**: `app/src/main/java/com/kma/base/screen/OnboardingScreen.kt`
6. ✏️ **Theme colors**: `app/src/main/res/values/colors.xml`
7. ✏️ **Navigation items**: `app/src/main/java/com/kma/base/NavigationItem.kt`

### 🔧 Tùy chọn:
8. ✏️ **Splash animation**: `app/src/main/java/com/kma/base/MainActivity.kt`
9. ✏️ **Typography**: `app/src/main/java/com/kma/base/ui/theme/Type.kt`

---

## Lưu Ý Quan Trọng

### Theme System
- App có 3 chế độ: **Light**, **Dark**, **Follow System**
- Theme được lưu trong `SharedPreferences` với key `"theme_mode"`
- Splash screen sẽ theo system theme của điện thoại (không theo app setting)

### Onboarding
- Chỉ hiển thị lần đầu mở app
- Có thể bỏ qua bằng nút "Skip"
- Người dùng chọn theme ở trang cuối onboarding

### Navigation
- Sử dụng Jetpack Navigation Compose
- Bottom Navigation với animation slide
- Có thể thêm/xóa tabs dễ dàng

---

## Troubleshooting

### Lỗi build không thành công
```bash
# Clean và rebuild
./gradlew clean
./gradlew build
```

### Logo không hiển thị
- Kiểm tra tên file phải là `applogo2.png`
- Kiểm tra file có ở đúng folder `drawable/`
- Xóa folder `build/` và rebuild

### Theme không đổi
- Clear app data: Settings → Apps → App → Clear data
- Hoặc uninstall và cài lại app

### Icon navigation không hiển thị
- Đảm bảo icon ở format XML (vector drawable) hoặc PNG
- Đặt tên file không có ký tự đặc biệt, chỉ dùng: `a-z`, `0-9`, `_`

---

## Contact & Support

- **Author**: Base Project Template
- **Version**: 1.0
- **Last Updated**: November 2025

Chúc bạn phát triển app thành công! 🚀
#   f o r u m - k m a - a p p  
 