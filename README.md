# Forum KMA - Android App

<p align="center">
  <img src="app/src/main/res/drawable/applogo2.png" width="120" alt="Forum KMA Logo">
</p>

<p align="center">
  <strong>Ứng dụng mạng xã hội dành cho sinh viên Học viện Kỹ thuật Mật mã (KMA)</strong>
</p>

<p align="center">
  <a href="#tính-năng">Tính năng</a> •
  <a href="#công-nghệ">Công nghệ</a> •
  <a href="#cài-đặt">Cài đặt</a> •
  <a href="#cấu-trúc-dự-án">Cấu trúc</a> •
  <a href="#api-endpoints">API</a>
</p>

---

## 📱 Giới Thiệu

Forum KMA là ứng dụng Android dành cho sinh viên Học viện Kỹ thuật Mật mã, cho phép người dùng:
- Chia sẻ bài viết, hình ảnh
- Nhắn tin trực tiếp với bạn bè
- Tương tác với cộng đồng KMA
- Quản lý profile cá nhân

---

## ✨ Tính Năng

### 🏠 Trang Chủ (Feed)
- Xem danh sách bài viết của cộng đồng
- Like và bình luận bài viết
- Tạo bài viết mới với hình ảnh
- Xem chi tiết bài viết

### 💬 Nhắn Tin
- Chat trực tiếp với bạn bè
- Hiển thị avatar và tên người dùng
- WebSocket real-time messaging
- Danh sách cuộc trò chuyện

### 👤 Profile
- Xem và chỉnh sửa thông tin cá nhân
- Thay đổi ảnh đại diện
- Xem danh sách bài viết của bản thân

### 👥 Bạn Bè
- Tìm kiếm người dùng
- Gửi/nhận lời mời kết bạn
- Quản lý danh sách bạn bè

### 🔔 Thông Báo
- Nhận thông báo khi có người thích/bình luận
- Thông báo lời mời kết bạn
- Thông báo tin nhắn mới

### ⚙️ Cài Đặt
- Chọn theme: Light / Dark / System
- Đăng xuất

---

## 🛠 Công Nghệ

| Công nghệ | Mô tả |
|-----------|-------|
| **Kotlin** | Ngôn ngữ lập trình chính |
| **Jetpack Compose** | Modern UI toolkit |
| **Material Design 3** | Design system |
| **Navigation Compose** | Điều hướng màn hình |
| **Retrofit** | HTTP client cho API calls |
| **OkHttp** | WebSocket & networking |
| **Coil** | Image loading library |
| **Coroutines + Flow** | Async programming |
| **ViewModel** | State management |

---

## 📦 Cài Đặt

### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 17+
- Android SDK 34+
- Kotlin 1.9+

### Bước 1: Clone repository
```bash
git clone https://github.com/Hnd8604/forum-kma-app.git
cd forum-kma-app
```

### Bước 2: Mở trong Android Studio
1. Mở Android Studio
2. File → Open → Chọn thư mục project
3. Đợi Gradle sync hoàn tất

### Bước 3: Cấu hình Backend URL
Mở file `app/src/main/java/com/kma/base/data/network/NetworkModule.kt`:
```kotlin
private const val BASE_URL = "http://YOUR_SERVER_IP:8080/api/v1/"
```

### Bước 4: Build và chạy
```bash
./gradlew assembleDebug
```
Hoặc nhấn **Run** trong Android Studio.

---

## 📁 Cấu Trúc Dự Án

```
app/src/main/java/com/kma/base/
├── MainActivity.kt              # Activity chính
├── MainScreen.kt                # Main screen với bottom navigation
├── AppNavigation.kt             # Navigation routes
├── BottomNavGraph.kt            # Bottom navigation graph
│
├── data/
│   ├── api/
│   │   └── ApiServices.kt       # Retrofit API interfaces
│   ├── model/
│   │   ├── ApiModels.kt         # Data classes cho API
│   │   ├── UserModels.kt        # User-related models
│   │   ├── PostModels.kt        # Post-related models
│   │   └── ...
│   ├── network/
│   │   └── NetworkModule.kt     # Retrofit & OkHttp config
│   ├── websocket/
│   │   └── ChatWebSocketManager.kt  # WebSocket cho chat
│   ├── local/
│   │   └── TokenManager.kt      # JWT token management
│   └── repository/
│       └── PostRepository.kt    # Data repository
│
├── screen/
│   ├── HomeScreen.kt            # Trang chủ - Feed
│   ├── MessagesScreen.kt        # Danh sách tin nhắn
│   ├── ChatDetailScreen.kt      # Chi tiết chat
│   ├── ProfileScreen.kt         # Trang cá nhân
│   ├── EditProfileScreen.kt     # Chỉnh sửa profile
│   ├── PostDetailScreen.kt      # Chi tiết bài viết
│   ├── CreatePostScreen.kt      # Tạo bài viết
│   ├── LoginScreen.kt           # Đăng nhập
│   ├── RegisterScreen.kt        # Đăng ký
│   ├── FriendsScreen.kt         # Quản lý bạn bè
│   ├── NotificationsScreen.kt   # Thông báo
│   ├── SettingsScreen.kt        # Cài đặt
│   └── OnboardingScreen.kt      # Onboarding
│
├── viewmodel/
│   ├── AuthViewModel.kt         # Authentication state
│   ├── HomeViewModel.kt         # Home screen state
│   ├── ChatViewModel.kt         # Chat state
│   ├── PostDetailViewModel.kt   # Post detail state
│   └── ...
│
├── model/
│   ├── AppTheme.kt              # Theme enum
│   └── BottomNavBarItem.kt      # Navigation items
│
└── ui/theme/
    ├── Color.kt                 # Định nghĩa màu
    ├── Theme.kt                 # Theme setup
    └── Type.kt                  # Typography
```

---

## 🔌 API Endpoints

App kết nối với backend qua các API sau:

### Authentication
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/auth/login` | Đăng nhập |
| POST | `/auth/register` | Đăng ký |
| POST | `/auth/logout` | Đăng xuất |

### User
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/users/me` | Lấy thông tin user hiện tại |
| GET | `/users/{id}` | Lấy thông tin user theo ID |
| PUT | `/users/me` | Cập nhật profile |

### Posts
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/posts/feed` | Lấy feed bài viết |
| GET | `/posts/{id}` | Chi tiết bài viết |
| POST | `/posts` | Tạo bài viết mới |
| DELETE | `/posts/{id}` | Xóa bài viết |

### Comments
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/comments/post/{postId}` | Lấy comments của bài viết |
| POST | `/comments` | Tạo comment mới |

### Chat
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/chat/conversations` | Danh sách cuộc trò chuyện |
| GET | `/chat/conversations/{id}/messages` | Tin nhắn trong cuộc trò chuyện |
| POST | `/chat/messages` | Gửi tin nhắn |

### WebSocket
```
ws://SERVER_IP:8090/ws?userId={userId}
```

---

## 🎨 Tùy Chỉnh

### Thay đổi màu sắc chủ đạo
File: `app/src/main/java/com/kma/base/ui/theme/Color.kt`

### Thay đổi logo
- Logo: `app/src/main/res/drawable/applogo2.png`
- Icon launcher: `app/src/main/res/mipmap-*/ic_launcher.png`

### Thay đổi tên app
File: `app/src/main/res/values/strings.xml`
```xml
<string name="app_name">Forum KMA</string>
```

---

## 🔧 Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run tests
./gradlew test

# Stop Gradle daemon
./gradlew --stop
```

APK output: `app/build/outputs/apk/`

---

## 🐛 Troubleshooting

### Lỗi: File bị lock khi build
```bash
./gradlew --stop
Remove-Item -Recurse -Force "app/build"
./gradlew assembleDebug
```

### Lỗi: Không thể kết nối API
- Kiểm tra `BASE_URL` trong `NetworkModule.kt`
- Đảm bảo backend đang chạy
- Kiểm tra kết nối mạng

### Lỗi: WebSocket không hoạt động
- Kiểm tra URL WebSocket
- Đảm bảo user đã đăng nhập
- Kiểm tra token còn hiệu lực

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Contributors

- **Hnd8604** - *Developer*

---

## 📞 Contact

- **GitHub**: [Hnd8604](https://github.com/Hnd8604)
- **Repository**: [forum-kma-app](https://github.com/Hnd8604/forum-kma-app)

---

<p align="center">
  Made with ❤️ for KMA Students
</p>