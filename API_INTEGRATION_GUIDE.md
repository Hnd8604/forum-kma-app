# Forum KMA Android App - API Integration Guide

## 📱 Tổng quan

App Android cho Forum KMA đã được tích hợp với backend API tại `http://72.60.198.235:8080`

## 🏗️ Cấu trúc dự án

```
app/src/main/java/com/kma/base/
├── data/
│   ├── api/
│   │   └── ApiServices.kt          # Retrofit API interfaces
│   ├── model/
│   │   └── ApiModels.kt            # Data models (Request/Response)
│   ├── network/
│   │   └── NetworkModule.kt        # Retrofit configuration
│   └── repository/
│       └── Repositories.kt         # Repository layer
├── screen/
│   ├── Home.kt                     # Trang chủ với feed posts
│   ├── MessagesScreen.kt           # Màn hình tin nhắn
│   ├── Profile.kt                  # Trang cá nhân
│   └── Settings.kt                 # Cài đặt
└── model/
    └── BottomNavBarItem.kt         # Navigation items
```

## 🔌 API Endpoints đã tích hợp

### 1. Authentication API (`/api/v1/auth/`)
- ✅ `POST /login` - Đăng nhập
- ✅ `POST /register` - Đăng ký
- ✅ `POST /logout` - Đăng xuất
- ✅ `POST /refresh` - Refresh token

### 2. User API (`/api/v1/users/`)
- ✅ `GET /me` - Lấy thông tin user hiện tại
- ✅ `GET /{id}` - Lấy thông tin user theo ID
- ✅ `GET /search` - Tìm kiếm users
- ✅ `PUT /me` - Cập nhật profile

### 3. Post API (`/api/v1/posts/`)
- ✅ `GET /feed` - Lấy feed posts (trang chủ)
- ✅ `GET /feed/group/{groupId}` - Lấy posts theo group
- ✅ `GET /{id}` - Lấy chi tiết post
- ✅ `POST /` - Tạo post mới
- ✅ `PUT /{id}` - Cập nhật post
- ✅ `DELETE /{id}` - Xóa post
- ✅ `GET /author/{id}` - Lấy posts theo tác giả

### 4. Interaction API (`/api/v1/interactions/`)
- ✅ `POST /react` - React vào post
- ✅ `DELETE /react` - Xóa reaction
- ✅ `GET /post/{postId}/reactions` - Lấy reactions của post

### 5. Comment API (`/api/v1/comments/`)
- ✅ `GET /post/{postId}` - Lấy comments của post
- ✅ `POST /` - Tạo comment
- ✅ `DELETE /{id}` - Xóa comment

### 6. Chat API (`/api/v1/chat/`)
- ✅ `GET /conversations` - Lấy danh sách conversations
- ✅ `GET /messages` - Lấy messages theo conversation
- ✅ `POST /send` - Gửi message
- ✅ `POST /conversations/{id}/mark-as-read` - Đánh dấu đã đọc

## 📦 Dependencies đã thêm

```kotlin
// Retrofit for API calls
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coil for image loading
implementation("io.coil-kt:coil-compose:2.5.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

// DataStore for preferences
implementation("androidx.datastore:datastore-preferences:1.0.0")
```

## 🚀 Cách sử dụng API trong code

### 1. Login Example

```kotlin
import com.kma.base.data.repository.AuthRepository
import kotlinx.coroutines.launch

// In your ViewModel or Composable
val authRepository = AuthRepository()

viewModelScope.launch {
    val result = authRepository.login(
        email = "user@example.com",
        password = "password123"
    )
    
    result.onSuccess { authResponse ->
        // Login successful
        val user = authResponse.user
        val token = authResponse.accessToken
        // Navigate to home screen
    }.onFailure { error ->
        // Show error message
        println("Login failed: ${error.message}")
    }
}
```

### 2. Get Feed Posts Example

```kotlin
import com.kma.base.data.repository.PostRepository

val postRepository = PostRepository()

viewModelScope.launch {
    val result = postRepository.getFeed(page = 0, limit = 10)
    
    result.onSuccess { pageResponse ->
        val posts = pageResponse.content
        // Update UI with posts
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

### 3. Get Conversations Example

```kotlin
import com.kma.base.data.repository.ChatRepository

val chatRepository = ChatRepository()

viewModelScope.launch {
    val result = chatRepository.getConversations()
    
    result.onSuccess { conversations ->
        // Update UI with conversations
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

### 4. Send Message Example

```kotlin
viewModelScope.launch {
    val result = chatRepository.sendMessage(
        conversationId = "conv123",
        content = "Hello!"
    )
    
    result.onSuccess { message ->
        // Message sent successfully
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

## 🔐 Authentication Flow

1. **Login/Register** → Nhận `accessToken` và `refreshToken`
2. Token được lưu tự động trong `NetworkModule`
3. Mọi request sau đó sẽ tự động thêm `Authorization: Bearer {token}` header
4. Khi token hết hạn, gọi `/auth/refresh` để lấy token mới

## 📱 Màn hình đã implement

### 1. **HomeScreen** (`Home.kt`)
- ✅ Hiển thị feed posts
- ✅ Tab navigation (Mới nhất, Phổ biến, Theo dõi)
- ✅ Post card với like, comment, share
- ✅ FAB để tạo post mới
- 🔄 **TODO**: Tích hợp API để load posts thật

### 2. **MessagesScreen** (`MessagesScreen.kt`)
- ✅ Danh sách conversations
- ✅ Search bar
- ✅ Unread count badge
- ✅ Group chat indicator
- 🔄 **TODO**: Tích hợp API để load conversations thật

### 3. **ProfileScreen** (`Profile.kt`)
- ✅ User info (avatar, name, bio)
- ✅ Stats (posts, followers, following)
- ✅ Menu items (profile, posts, saved, etc.)
- ✅ Logout button
- 🔄 **TODO**: Tích hợp API để load user data thật

### 4. **SettingsScreen** (`Settings.kt`)
- ✅ Theme selection (Light/Dark/System)
- ✅ Theme được lưu trong SharedPreferences

## 🎨 UI Components

Tất cả màn hình đều sử dụng:
- ✅ Material Design 3
- ✅ Jetpack Compose
- ✅ Dark mode support
- ✅ Responsive layout
- ✅ Modern animations

## 📝 Next Steps - Tích hợp API thật

### Bước 1: Tạo ViewModel cho mỗi màn hình

```kotlin
// Example: HomeViewModel.kt
class HomeViewModel : ViewModel() {
    private val postRepository = PostRepository()
    
    private val _posts = MutableStateFlow<List<PostWithInteractionResponse>>(emptyList())
    val posts: StateFlow<List<PostWithInteractionResponse>> = _posts
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postRepository.getFeed()
            result.onSuccess { pageResponse ->
                _posts.value = pageResponse.content
            }
            _isLoading.value = false
        }
    }
}
```

### Bước 2: Sử dụng ViewModel trong Screen

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadPosts()
    }
    
    // UI code...
}
```

### Bước 3: Tạo Login Screen

Hiện tại app chưa có màn hình login. Bạn cần:
1. Tạo `LoginScreen.kt`
2. Tạo `LoginViewModel.kt`
3. Thêm navigation route cho login
4. Check authentication state khi app khởi động

### Bước 4: Lưu trữ token persistent

Sử dụng DataStore để lưu token:

```kotlin
// TokenManager.kt
class TokenManager(private val context: Context) {
    private val dataStore = context.dataStore
    
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
    
    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")
    }
}
```

## 🐛 Debugging

### Enable logging
Logging đã được bật sẵn trong `NetworkModule.kt`:

```kotlin
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

Xem logs trong Logcat với filter "OkHttp"

### Common Issues

1. **Network Error**: Kiểm tra INTERNET permission trong AndroidManifest
2. **401 Unauthorized**: Token hết hạn hoặc chưa login
3. **Connection refused**: Kiểm tra IP và port của backend
4. **SSL Error**: Đã thêm `usesCleartextTraffic="true"` cho HTTP

## 📚 Tài liệu tham khảo

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material Design 3](https://m3.material.io/)

## 🎯 Roadmap

- [ ] Implement Login/Register screens
- [ ] Integrate real API data into all screens
- [ ] Add pull-to-refresh
- [ ] Add pagination for lists
- [ ] Implement image upload
- [ ] Add WebSocket for real-time chat
- [ ] Add push notifications
- [ ] Add offline support with Room database
- [ ] Add error handling UI
- [ ] Add loading states
- [ ] Add empty states
- [ ] Write unit tests

---

**Tác giả**: Forum KMA Team  
**Ngày cập nhật**: 30/12/2024  
**Phiên bản**: 1.0.0
