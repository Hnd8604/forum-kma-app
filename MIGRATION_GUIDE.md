# 🔄 Hướng Dẫn Migration - Di Chuyển Code Sang Clean Architecture

## 📋 Tổng Quan
Tài liệu này hướng dẫn chi tiết cách di chuyển code hiện tại từ cấu trúc `com.kma.base` sang cấu trúc Clean Architecture mới.

## 🗂️ Mapping Thư Mục Cũ → Mới

### Cấu trúc hiện tại (OLD)
```
com/kma/base/
├── MainActivity.kt
├── MainScreen.kt
├── AppNavigation.kt
├── BottomNavGraph.kt
├── NavScreen.kt
├── data/
│   ├── api/
│   ├── local/
│   ├── model/
│   ├── network/
│   └── repository/
├── model/
│   ├── AppTheme.kt
│   └── BottomNavBarItem.kt
├── screen/
│   ├── Home.kt
│   ├── LoginScreen.kt
│   ├── RegisterScreen.kt
│   ├── Profile.kt
│   ├── MessagesScreen.kt
│   ├── Settings.kt
│   ├── SplashScreen.kt
│   └── OnboardingScreen.kt
├── ui/
│   └── theme/
└── viewmodel/
    └── AuthViewModel.kt
```

### Cấu trúc mới (NEW)
```
com/kma/forumkma/
├── core/                           # ← di chuyển data/network, data/local vào đây
│   ├── base/
│   ├── di/                        # ← DI setup (mới)
│   ├── network/                   # ← data/network
│   ├── local/                     # ← data/local
│   └── utils/                     # ← utilities mới
│
├── data/                          # ← data/api, data/model, data/repository
│   ├── remote/
│   │   ├── dto/                   # ← data/model (API models)
│   │   └── datasource/            # ← data/api
│   ├── local/
│   │   └── entities/
│   ├── mapper/                    # ← mapper mới (DTO ↔ Domain)
│   └── repository/                # ← data/repository (implementations)
│
├── domain/                        # ← Layer mới
│   ├── model/                     # ← Business models
│   ├── repository/                # ← Repository interfaces
│   └── usecase/                   # ← Business logic
│
└── presentation/                  # ← screen/, viewmodel/, ui/
    ├── MainActivity.kt            # ← MainActivity
    ├── navigation/                # ← AppNavigation, BottomNavGraph, NavScreen
    ├── theme/                     # ← ui/theme
    ├── components/                # ← Common components
    └── features/                  # ← screen/ (reorganize by feature)
        ├── splash/                # ← SplashScreen
        ├── onboarding/            # ← OnboardingScreen
        ├── auth/
        │   ├── login/             # ← LoginScreen + AuthViewModel
        │   └── register/          # ← RegisterScreen
        ├── home/                  # ← Home + MainScreen
        ├── profile/               # ← Profile
        ├── messages/              # ← MessagesScreen
        └── settings/              # ← Settings
```

## 📝 Chi Tiết Migration Từng File

### 1. Core Layer

#### 1.1 Network (data/network → core/network)
```bash
# Di chuyển
com/kma/base/data/network/*
→ com/kma/forumkma/core/network/

# Files cần di chuyển:
- ApiService / RetrofitClient → core/network/ApiService.kt
- NetworkConfigs → core/network/NetworkConfig.kt
```

#### 1.2 Local Storage (data/local → core/local)
```bash
# Di chuyển
com/kma/base/data/local/*
→ com/kma/forumkma/core/local/

# Files:
- SharedPreferences wrapper → core/local/SharedPrefsManager.kt
- Room Database → core/local/AppDatabase.kt
- DAOs → core/local/dao/
```

### 2. Data Layer

#### 2.1 API Models → DTOs
```bash
# Di chuyển
com/kma/base/data/model/*
→ com/kma/forumkma/data/remote/dto/

# Tổ chức lại theo module:
- UserDto, LoginRequest, LoginResponse → dto/auth/
- PostDto, CreatePostRequest → dto/post/
- CommentDto → dto/comment/
```

#### 2.2 API Calls → DataSource
```bash
# Di chuyển
com/kma/base/data/api/*
→ com/kma/forumkma/data/remote/datasource/

# Refactor:
- AuthApi → AuthRemoteDataSource.kt
- PostApi → PostRemoteDataSource.kt
```

#### 2.3 Repository Implementations
```bash
# Di chuyển
com/kma/base/data/repository/*
→ com/kma/forumkma/data/repository/

# Refactor:
- AuthRepository → AuthRepositoryImpl.kt
  (Implement AuthRepository interface từ domain)
- PostRepository → PostRepositoryImpl.kt
```

### 3. Domain Layer (MỚI)

#### 3.1 Tạo Domain Models
```kotlin
// Từ DTO → Domain Model
// OLD: data/model/UserDto.kt
data class UserDto(
    val id: String,
    val username: String,
    ...
)

// NEW: domain/model/User.kt
data class User(
    val id: String,
    val username: String,
    ...
) {
    // Business logic
    fun getDisplayName(): String = ...
}
```

#### 3.2 Tạo Repository Interfaces
```kotlin
// NEW: domain/repository/AuthRepository.kt
interface AuthRepository {
    suspend fun login(username: String, password: String): Resource<User>
    suspend fun register(...): Resource<User>
}

// Implementation: data/repository/AuthRepositoryImpl.kt
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val mapper: UserMapper
) : AuthRepository {
    override suspend fun login(...): Resource<User> {
        // Call API, map DTO to Domain model
    }
}
```

#### 3.3 Tạo UseCases
```kotlin
// Tách business logic từ ViewModel ra UseCase
// OLD: viewmodel/AuthViewModel.kt
fun login() {
    viewModelScope.launch {
        // validation
        // call repository
        // handle result
    }
}

// NEW: domain/usecase/auth/LoginUseCase.kt
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(params: Params): Resource<User> {
        // validation
        // call repository
        return authRepository.login(...)
    }
}

// ViewModel chỉ gọi UseCase:
fun login() {
    launchCatching {
        val result = loginUseCase(params)
        // handle result
    }
}
```

### 4. Presentation Layer

#### 4.1 Navigation
```bash
# Di chuyển
AppNavigation.kt, BottomNavGraph.kt, NavScreen.kt
→ presentation/navigation/

# Refactor:
- NavScreen.kt → Screen.kt (sealed class cho routes)
- AppNavigation.kt → NavGraph.kt
- BottomNavGraph.kt → BottomNavGraph.kt (giữ nguyên)
```

#### 4.2 Theme
```bash
# Di chuyển
ui/theme/*
→ presentation/theme/

# Restructure:
- Color.kt, Theme.kt, Type.kt, Shape.kt
```

#### 4.3 Screens → Features
```bash
# Reorganize screens theo feature

OLD: screen/LoginScreen.kt
NEW: presentation/features/auth/login/
     ├── LoginScreen.kt        # UI
     ├── LoginViewModel.kt     # Logic
     └── LoginState.kt         # State & Events

OLD: screen/Home.kt + screen/MainScreen.kt
NEW: presentation/features/home/
     ├── HomeScreen.kt
     ├── HomeViewModel.kt
     └── components/
         ├── PostItem.kt
         └── PostListItem.kt
```

#### 4.4 ViewModels
```bash
# Di chuyển và refactor ViewModels

OLD: viewmodel/AuthViewModel.kt
NEW: Split theo feature:
     - presentation/features/auth/login/LoginViewModel.kt
     - presentation/features/auth/register/RegisterViewModel.kt

# Refactor pattern:
1. Extend BaseViewModel<State, Event>
2. Define UIState data class
3. Define UIEvent sealed class
4. Inject UseCases (không inject Repository trực tiếp)
5. Handle events in onEvent()
```

### 5. MainActivity
```bash
# Di chuyển
MainActivity.kt, MainScreen.kt
→ presentation/

# Update imports và navigation setup
```

## 🔧 Các Bước Thực Hiện

### Bước 1: Setup Dependencies (build.gradle.kts)
```kotlin
// Thêm dependencies cần thiết
dependencies {
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")
}
```

### Bước 2: Tạo Base Classes
✅ Đã tạo sẵn:
- `core/base/BaseViewModel.kt`
- `core/base/BaseUseCase.kt`
- `core/utils/Constants.kt`
- `core/utils/Extensions.kt`

### Bước 3: Setup Dependency Injection
```kotlin
// core/di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            Constants.PREF_NAME,
            Context.MODE_PRIVATE
        )
    }
}

// core/di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .build()
            
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

// core/di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
```

### Bước 4: Create Domain Layer
```
1. Tạo Domain Models (domain/model/)
   - User.kt
   - Post.kt
   - Comment.kt
   - Group.kt

2. Tạo Repository Interfaces (domain/repository/)
   - AuthRepository.kt
   - PostRepository.kt
   - CommentRepository.kt

3. Tạo UseCases (domain/usecase/)
   - auth/LoginUseCase.kt
   - auth/RegisterUseCase.kt
   - post/GetPostsUseCase.kt
   - post/CreatePostUseCase.kt
```

### Bước 5: Implement Data Layer
```
1. Tạo DTOs (data/remote/dto/)
   - Từ data/model cũ

2. Tạo Mappers (data/mapper/)
   - UserMapper.kt (DTO ↔ Domain)
   - PostMapper.kt

3. Implement Repositories (data/repository/)
   - AuthRepositoryImpl.kt
   - PostRepositoryImpl.kt
```

### Bước 6: Refactor Presentation Layer
```
1. Di chuyển Screens theo features
2. Tách ViewModels
3. Tạo State & Event classes
4. Update Navigation
```

### Bước 7: Test & Verify
```
1. Build project
2. Fix imports
3. Test từng feature
4. Remove code cũ
```

## ⚠️ Lưu Ý Quan Trọng

### 1. Package Naming
- **OLD**: `com.kma.base`
- **NEW**: `com.kma.forumkma`
- Update trong `AndroidManifest.xml`, `build.gradle.kts`

### 2. Dependency Rules
```
✅ ĐÚNG:
presentation → domain → data → core
ViewModel → UseCase → Repository → DataSource

❌ SAI:
domain → data (domain KHÔNG được phụ thuộc data)
ViewModel → Repository (phải qua UseCase)
```

### 3. Naming Conventions
```kotlin
// Models
data class User              // Domain model
data class UserDto           // Data Transfer Object
data class UserEntity        // Room entity

// Repositories
interface AuthRepository     // Interface (domain)
class AuthRepositoryImpl     // Implementation (data)

// UseCases
class LoginUseCase           // Use case
class GetPostsUseCase

// Screens & ViewModels
LoginScreen.kt               // Composable
LoginViewModel.kt            // ViewModel
LoginState.kt                // State & Events
```

## 📊 Checklist Migration

### Phase 1: Foundation ✅
- [x] Tạo cấu trúc thư mục
- [x] Tạo base classes
- [x] Setup constants & extensions
- [ ] Setup Hilt DI
- [ ] Setup Retrofit & Room

### Phase 2: Domain Layer
- [ ] Tạo domain models
- [ ] Tạo repository interfaces
- [ ] Tạo use cases

### Phase 3: Data Layer
- [ ] Di chuyển DTOs
- [ ] Tạo mappers
- [ ] Implement repositories
- [ ] Tạo data sources

### Phase 4: Presentation Layer
- [ ] Di chuyển navigation
- [ ] Di chuyển theme
- [ ] Refactor screens theo features
- [ ] Refactor ViewModels
- [ ] Tạo State classes

### Phase 5: Testing & Cleanup
- [ ] Build & fix errors
- [ ] Test app
- [ ] Remove old code
- [ ] Update documentation

## 🎯 Kết Quả Mong Đợi

Sau khi hoàn thành migration:
- ✅ Code dễ đọc, dễ maintain
- ✅ Tách biệt rõ ràng các layers
- ✅ Dễ test (unit test cho UseCase, ViewModel)
- ✅ Dễ scale và thêm features mới
- ✅ Tuân thủ SOLID principles
- ✅ Clean Architecture chuẩn

## 📚 Ví Dụ Code
Xem chi tiết các file đã tạo sẵn:
- `core/base/BaseViewModel.kt`
- `core/base/BaseUseCase.kt`
- `domain/model/User.kt`
- `domain/repository/AuthRepository.kt`
- `domain/usecase/auth/LoginUseCase.kt`
- `presentation/features/auth/login/LoginViewModel.kt`
- `presentation/features/auth/login/LoginState.kt`

---

**Chúc bạn migration thành công! 🚀**

*Nếu có vấn đề gì, tham khảo thêm tại:*
- [ANDROID_STRUCTURE_GUIDE.md](./ANDROID_STRUCTURE_GUIDE.md)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
