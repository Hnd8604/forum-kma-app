# 📱 Clean Architecture Android - Quick Start

## 🎯 Tổng Quan

Dự án Android này được tổ chức theo kiến trúc **Clean Architecture + MVVM**, sử dụng **Kotlin** và **Jetpack Compose**.

## 📚 Tài Liệu

| Tài liệu | Mô tả |
|----------|--------|
| [**ANDROID_STRUCTURE_GUIDE.md**](./ANDROID_STRUCTURE_GUIDE.md) | Hướng dẫn chi tiết về cấu trúc Clean Architecture |
| [**MIGRATION_GUIDE.md**](./MIGRATION_GUIDE.md) | Hướng dẫn di chuyển code cũ sang cấu trúc mới |

## 🚀 Bắt Đầu Nhanh

### 1. Cấu Trúc Đã Được Tạo

Chạy lệnh sau để tạo cấu trúc thư mục:
```powershell
.\create_structure.ps1
```

Hoặc đã được tự động tạo với cấu trúc:
```
app/src/main/java/com/kma/forumkma/
├── core/           # Core utilities, base classes, DI
├── data/           # Data layer (API, Database, Repository implementations)
├── domain/         # Domain layer (Models, Repository interfaces, UseCases)
└── presentation/   # UI layer (Screens, ViewModels, Navigation)
```

### 2. Files Mẫu Đã Tạo

✅ **Core Layer**
- `core/base/BaseViewModel.kt` - Base ViewModel với state management
- `core/base/BaseUseCase.kt` - Base UseCase & Resource wrapper
- `core/network/interceptors/AuthInterceptor.kt` - Auto add Bearer token
- `core/network/interceptors/LoggingInterceptor.kt` - HTTP logging
- `core/utils/Constants.kt` - App constants
- `core/utils/Extensions.kt` - Kotlin extensions

✅ **Domain Layer**
- `domain/model/User.kt` - User domain model
- `domain/model/Post.kt` - Post & Group models
- `domain/repository/AuthRepository.kt` - Auth repository interface
- `domain/usecase/auth/LoginUseCase.kt` - Login use case

✅ **Presentation Layer**
- `presentation/features/auth/login/LoginState.kt` - Login UI state & events
- `presentation/features/auth/login/LoginViewModel.kt` - Login ViewModel

## 📦 Dependencies Cần Thiết

Thêm vào `app/build.gradle.kts`:

```kotlin
plugins {
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Coil (Image loading)
    implementation("io.coil-kt:coil-compose:2.5.0")
}
```

Thêm vào `build.gradle.kts` (project level):
```kotlin
plugins {
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
```

## 🏗️ Kiến Trúc Chi Tiết

### Data Flow
```
UI (Composable)
    ↕️
ViewModel (State Management)
    ↕️
UseCase (Business Logic)
    ↕️
Repository Interface (Contract)
    ↕️
Repository Implementation (Data Layer)
    ↕️
DataSource (API / Database)
```

### Dependency Rules
```
✅ presentation → domain → data → core
❌ domain KHÔNG được phụ thuộc vào data hoặc presentation
```

## 💡 Ví Dụ Sử dụng

### 1. Tạo Feature Mới

#### Step 1: Domain Model
```kotlin
// domain/model/Post.kt
data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: User,
    val createdAt: Date
)
```

#### Step 2: Repository Interface
```kotlin
// domain/repository/PostRepository.kt
interface PostRepository {
    suspend fun getPosts(page: Int): Resource<List<Post>>
    suspend fun createPost(post: Post): Resource<Post>
}
```

#### Step 3: UseCase
```kotlin
// domain/usecase/post/GetPostsUseCase.kt
class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) : BaseUseCase<Int, Resource<List<Post>>>() {
    override suspend fun invoke(page: Int): Resource<List<Post>> {
        return repository.getPosts(page)
    }
}
```

#### Step 4: Repository Implementation
```kotlin
// data/repository/PostRepositoryImpl.kt
class PostRepositoryImpl @Inject constructor(
    private val remoteDataSource: PostRemoteDataSource,
    private val mapper: PostMapper
) : PostRepository {
    override suspend fun getPosts(page: Int): Resource<List<Post>> {
        return try {
            val response = remoteDataSource.getPosts(page)
            val posts = response.map { mapper.toDomain(it) }
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}
```

#### Step 5: UI State & ViewModel
```kotlin
// presentation/features/home/HomeState.kt
data class HomeState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HomeEvent {
    object LoadPosts : HomeEvent()
    object Refresh : HomeEvent()
}

// presentation/features/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : BaseViewModel<HomeState, HomeEvent>(HomeState()) {
    
    init {
        loadPosts()
    }
    
    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadPosts -> loadPosts()
            is HomeEvent.Refresh -> loadPosts()
        }
    }
    
    private fun loadPosts() {
        launchCatching {
            val result = getPostsUseCase(page = 1)
            result.onSuccess { posts ->
                updateState { copy(posts = posts) }
            }
        }
    }
}
```

#### Step 6: Composable UI
```kotlin
// presentation/features/home/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    Column {
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorView(state.error!!)
            else -> PostList(posts = state.posts)
        }
    }
}
```

## 📝 Checklist Migration

- [ ] Đọc [ANDROID_STRUCTURE_GUIDE.md](./ANDROID_STRUCTURE_GUIDE.md)
- [ ] Đọc [MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md)
- [ ] Cài đặt dependencies (Hilt, Retrofit, Room...)
- [ ] Setup Dependency Injection modules
- [ ] Tạo Domain models
- [ ] Tạo Repository interfaces
- [ ] Tạo UseCases
- [ ] Implement Repositories
- [ ] Refactor ViewModels
- [ ] Di chuyển Screens theo features
- [ ] Update Navigation
- [ ] Test & verify

## 🎨 Best Practices

### 1. Naming Conventions
```kotlin
// Models
User              // Domain model
UserDto           // API response
UserEntity        // Database entity

// Repositories
AuthRepository          // Interface (domain)
AuthRepositoryImpl      // Implementation (data)

// UseCases
LoginUseCase
GetPostsUseCase

// ViewModels & Screens
LoginViewModel
LoginScreen
LoginState
```

### 2. State Management
```kotlin
// Always use sealed class for UI State
data class ScreenState(
    val data: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Events as sealed class
sealed class ScreenEvent {
    object Load : ScreenEvent()
    data class ItemClicked(val id: String) : ScreenEvent()
}
```

### 3. Error Handling
```kotlin
// Use Resource wrapper
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

// Chain operations
result
    .onSuccess { data -> /* handle success */ }
    .onError { error -> /* handle error */ }
    .onLoading { /* show loading */ }
```

## 🧪 Testing

### Unit Test Example
```kotlin
// test/domain/usecase/LoginUseCaseTest.kt
class LoginUseCaseTest {
    @Test
    fun `login with valid credentials returns success`() = runTest {
        // Given
        val repository = mockk<AuthRepository>()
        val useCase = LoginUseCase(repository)
        
        coEvery { 
            repository.login(any(), any()) 
        } returns Resource.Success(mockUser)
        
        // When
        val result = useCase(LoginUseCase.Params("user", "pass"))
        
        // Then
        assertTrue(result is Resource.Success)
    }
}
```

## 📚 Tham Khảo

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Clean Architecture Blog](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Documentation](https://dagger.dev/hilt/)

## 🤝 Contribute

Khi thêm feature mới:
1. Tạo domain model → repository interface → use case
2. Implement repository trong data layer
3. Tạo ViewModel với State & Event
4. Tạo Composable UI
5. Wire up với DI (Hilt module nếu cần)

---

**Happy Coding! 🚀**
