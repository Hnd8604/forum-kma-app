# Hướng Dẫn Tích Hợp Backend - Forum KMA Android App

## Tổng Quan

App Android đã được tích hợp với các API endpoints chính từ backend Forum KMA:

### Các Chức Năng Đã Tích Hợp

1. **Authentication (Xác thực)**
   - ✅ Đăng nhập
   - ✅ Đăng ký
   - ✅ Đăng xuất
   - ✅ Refresh token tự động
   - ✅ Lưu trữ token an toàn (DataStore)

2. **Posts (Bài viết)**
   - ✅ Xem danh sách bài viết (Feed)
   - ✅ Xem chi tiết bài viết
   - ✅ Tạo bài viết mới
   - ✅ Like/Unlike bài viết
   - ✅ Xem bài viết theo nhóm

3. **Comments (Bình luận)**
   - ✅ Xem danh sách bình luận
   - ✅ Tạo bình luận mới
   - ✅ Like bình luận

4. **Groups (Nhóm)**
   - ✅ Xem danh sách nhóm
   - ✅ Tham gia/Rời nhóm
   - ✅ Tạo nhóm mới

5. **Notifications (Thông báo)**
   - ✅ Xem danh sách thông báo
   - ✅ Đánh dấu đã đọc

6. **Friends (Bạn bè)**
   - ✅ Xem danh sách bạn bè
   - ✅ Gửi/Chấp nhận lời mời kết bạn

7. **Chat (Tin nhắn)**
   - ✅ Xem danh sách cuộc trò chuyện
   - ✅ Gửi tin nhắn

---

## Cấu Hình

### 1. Thay Đổi BASE_URL

Mở file `app/src/main/java/com/kma/base/data/network/NetworkModule.kt`:

```kotlin
object NetworkModule {
    // Thay đổi địa chỉ server của bạn
    private const val BASE_URL = "http://YOUR_SERVER_IP:8080/"
    
    // Ví dụ:
    // - Emulator: "http://10.0.2.2:8080/"
    // - Thiết bị thật (cùng mạng): "http://192.168.1.100:8080/"
    // - Server thật: "http://your-domain.com:8080/"
}
```

### 2. Cho Phép HTTP (Nếu không dùng HTTPS)

Nếu server không có SSL, thêm vào `AndroidManifest.xml`:

```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

---

## Cấu Trúc Dự Án

```
app/src/main/java/com/kma/base/
├── data/
│   ├── api/
│   │   └── ApiServices.kt       # Định nghĩa API endpoints
│   ├── local/
│   │   └── TokenManager.kt      # Quản lý token
│   ├── model/
│   │   ├── ApiModels.kt         # Auth & Post models
│   │   ├── CommentModels.kt     # Comment models
│   │   ├── FriendModels.kt      # Friend models
│   │   ├── GroupModels.kt       # Group models
│   │   └── NotificationModels.kt # Notification models
│   ├── network/
│   │   └── NetworkModule.kt     # Retrofit configuration
│   └── repository/
│       └── Repositories.kt      # Data repositories
├── screen/
│   ├── HomeScreen.kt            # Trang chủ/Feed
│   ├── LoginScreen.kt           # Đăng nhập
│   ├── RegisterScreen.kt        # Đăng ký
│   ├── PostDetailScreen.kt      # Chi tiết bài viết
│   ├── CreatePostScreen.kt      # Tạo bài viết
│   ├── GroupsScreen.kt          # Danh sách nhóm
│   ├── FriendsScreen.kt         # Bạn bè
│   ├── NotificationsScreen.kt   # Thông báo
│   ├── MessagesScreen.kt        # Tin nhắn
│   ├── ProfileScreen.kt         # Trang cá nhân
│   └── SettingsScreen.kt        # Cài đặt
├── viewmodel/
│   ├── AuthViewModel.kt         # Xử lý authentication
│   ├── HomeViewModel.kt         # Xử lý home/feed
│   ├── PostDetailViewModel.kt   # Xử lý chi tiết bài viết
│   └── CreatePostViewModel.kt   # Xử lý tạo bài viết
├── AppNavigation.kt             # Navigation chính
├── BottomNavGraph.kt            # Bottom navigation
└── MainScreen.kt                # Main screen với bottom nav
```

---

## API Endpoints Được Sử Dụng

### Authentication
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | /api/v1/auth/login | Đăng nhập |
| POST | /api/v1/auth/register | Đăng ký |
| POST | /api/v1/auth/logout | Đăng xuất |
| POST | /api/v1/auth/refresh | Refresh token |

### Users
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | /api/v1/users/me | Lấy thông tin user hiện tại |
| GET | /api/v1/users/{id} | Lấy thông tin user theo ID |
| GET | /api/v1/users/search | Tìm kiếm users |

### Posts
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | /api/v1/posts/feed | Lấy danh sách bài viết |
| GET | /api/v1/posts/{id} | Lấy chi tiết bài viết |
| POST | /api/v1/posts | Tạo bài viết mới |
| PUT | /api/v1/posts/{id} | Cập nhật bài viết |
| DELETE | /api/v1/posts/{id} | Xóa bài viết |

### Comments
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | /api/v1/comments/post/{postId} | Lấy comments của post |
| POST | /api/v1/comments | Tạo comment mới |
| DELETE | /api/v1/comments/{id} | Xóa comment |

### Groups
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | /api/v1/groups | Lấy danh sách groups |
| GET | /api/v1/groups/my-groups | Lấy groups của user |
| POST | /api/v1/groups | Tạo group mới |
| POST | /api/v1/groups/join | Tham gia group |

---

## Chạy Ứng Dụng

1. Mở project bằng Android Studio
2. Đảm bảo đã cấu hình đúng BASE_URL
3. Kết nối thiết bị hoặc chạy emulator
4. Chạy: `./gradlew installDebug` hoặc click Run trong Android Studio

---

## Lưu Ý

- **Token Management**: Token được tự động thêm vào mỗi request thông qua interceptor
- **Error Handling**: Các lỗi API được hiển thị cho người dùng
- **Offline**: Hiện tại app cần kết nối internet để hoạt động
- **Image Upload**: Cần implement thêm tính năng upload ảnh qua file-service

---

## Tiếp Tục Phát Triển

Các tính năng có thể thêm:
- [ ] Upload ảnh cho bài viết
- [ ] Push notifications (Firebase)
- [ ] Real-time chat (WebSocket)
- [ ] Caching offline
- [ ] Pull-to-refresh
- [ ] Infinite scroll pagination
