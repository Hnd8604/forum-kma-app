# 📱 Hướng Dẫn Cấu Trúc Android App - Clean Architecture + MVVM

## 🎯 Mục Tiêu
Cấu trúc dự án Android theo chuẩn **Clean Architecture** kết hợp **MVVM Pattern** để:
- Tách biệt rõ ràng các layer (Domain, Data, Presentation)
- Dễ test, maintain và scale
- Code reusable và tuân thủ SOLID principles

## 📁 Cấu Trúc Thư Mục Chuẩn

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/kma/forumkma/
│   │   │   ├── core/                          # Core utilities & base classes
│   │   │   │   ├── base/
│   │   │   │   │   ├── BaseActivity.kt
│   │   │   │   │   ├── BaseFragment.kt
│   │   │   │   │   ├── BaseViewModel.kt
│   │   │   │   │   └── BaseUseCase.kt
│   │   │   │   ├── di/                        # Dependency Injection
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   └── RepositoryModule.kt
│   │   │   │   ├── utils/                     # Utility classes
│   │   │   │   │   ├── Constants.kt
│   │   │   │   │   ├── Extensions.kt
│   │   │   │   │   ├── DateUtils.kt
│   │   │   │   │   ├── NetworkUtils.kt
│   │   │   │   │   └── ValidationUtils.kt
│   │   │   │   ├── network/                   # Network layer
│   │   │   │   │   ├── ApiClient.kt
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   ├── NetworkResult.kt
│   │   │   │   │   └── interceptors/
│   │   │   │   │       ├── AuthInterceptor.kt
│   │   │   │   │       └── LoggingInterceptor.kt
│   │   │   │   └── local/                     # Local storage
│   │   │   │       ├── AppDatabase.kt
│   │   │   │       ├── SharedPrefsManager.kt
│   │   │   │       └── dao/
│   │   │   │
│   │   │   ├── data/                          # Data Layer
│   │   │   │   ├── remote/                    # Remote data sources
│   │   │   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   ├── LoginRequest.kt
│   │   │   │   │   │   │   ├── LoginResponse.kt
│   │   │   │   │   │   │   └── UserDto.kt
│   │   │   │   │   │   ├── post/
│   │   │   │   │   │   │   ├── PostDto.kt
│   │   │   │   │   │   │   └── CreatePostRequest.kt
│   │   │   │   │   │   └── comment/
│   │   │   │   │   └── datasource/
│   │   │   │   │       ├── AuthRemoteDataSource.kt
│   │   │   │   │       ├── PostRemoteDataSource.kt
│   │   │   │   │       └── CommentRemoteDataSource.kt
│   │   │   │   │
│   │   │   │   ├── local/                     # Local data sources
│   │   │   │   │   ├── entities/              # Room entities
│   │   │   │   │   │   ├── UserEntity.kt
│   │   │   │   │   │   ├── PostEntity.kt
│   │   │   │   │   │   └── CommentEntity.kt
│   │   │   │   │   └── datasource/
│   │   │   │   │       ├── UserLocalDataSource.kt
│   │   │   │   │       └── PostLocalDataSource.kt
│   │   │   │   │
│   │   │   │   ├── mapper/                    # Data mappers (DTO <-> Domain)
│   │   │   │   │   ├── UserMapper.kt
│   │   │   │   │   ├── PostMapper.kt
│   │   │   │   │   └── CommentMapper.kt
│   │   │   │   │
│   │   │   │   └── repository/                # Repository implementations
│   │   │   │       ├── AuthRepositoryImpl.kt
│   │   │   │       ├── PostRepositoryImpl.kt
│   │   │   │       └── CommentRepositoryImpl.kt
│   │   │   │
│   │   │   ├── domain/                        # Domain Layer (Business Logic)
│   │   │   │   ├── model/                     # Domain models
│   │   │   │   │   ├── User.kt
│   │   │   │   │   ├── Post.kt
│   │   │   │   │   ├── Comment.kt
│   │   │   │   │   └── Group.kt
│   │   │   │   │
│   │   │   │   ├── repository/                # Repository interfaces
│   │   │   │   │   ├── AuthRepository.kt
│   │   │   │   │   ├── PostRepository.kt
│   │   │   │   │   └── CommentRepository.kt
│   │   │   │   │
│   │   │   │   └── usecase/                   # Use cases (Business rules)
│   │   │   │       ├── auth/
│   │   │   │       │   ├── LoginUseCase.kt
│   │   │   │       │   ├── RegisterUseCase.kt
│   │   │   │       │   ├── LogoutUseCase.kt
│   │   │   │       │   └── GetCurrentUserUseCase.kt
│   │   │   │       ├── post/
│   │   │   │       │   ├── GetPostsUseCase.kt
│   │   │   │       │   ├── CreatePostUseCase.kt
│   │   │   │       │   ├── UpdatePostUseCase.kt
│   │   │   │       │   └── DeletePostUseCase.kt
│   │   │   │       └── comment/
│   │   │   │           ├── GetCommentsUseCase.kt
│   │   │   │           └── CreateCommentUseCase.kt
│   │   │   │
│   │   │   └── presentation/                  # Presentation Layer (UI)
│   │   │       ├── MainActivity.kt
│   │   │       ├── MainApplication.kt
│   │   │       │
│   │   │       ├── navigation/                # Navigation
│   │   │       │   ├── NavGraph.kt
│   │   │       │   ├── Screen.kt
│   │   │       │   └── NavigationComponent.kt
│   │   │       │
│   │   │       ├── theme/                     # UI Theme
│   │   │       │   ├── Color.kt
│   │   │       │   ├── Theme.kt
│   │   │       │   ├── Type.kt
│   │   │       │   └── Shape.kt
│   │   │       │
│   │   │       ├── components/                # Reusable UI components
│   │   │       │   ├── CustomButton.kt
│   │   │       │   ├── CustomTextField.kt
│   │   │       │   ├── LoadingDialog.kt
│   │   │       │   ├── ErrorDialog.kt
│   │   │       │   └── BottomNavBar.kt
│   │   │       │
│   │   │       └── features/                  # Feature modules
│   │   │           ├── splash/
│   │   │           │   ├── SplashScreen.kt
│   │   │           │   └── SplashViewModel.kt
│   │   │           │
│   │   │           ├── onboarding/
│   │   │           │   ├── OnboardingScreen.kt
│   │   │           │   └── OnboardingViewModel.kt
│   │   │           │
│   │   │           ├── auth/
│   │   │           │   ├── login/
│   │   │           │   │   ├── LoginScreen.kt
│   │   │           │   │   ├── LoginViewModel.kt
│   │   │           │   │   └── LoginState.kt
│   │   │           │   └── register/
│   │   │           │       ├── RegisterScreen.kt
│   │   │           │       ├── RegisterViewModel.kt
│   │   │           │       └── RegisterState.kt
│   │   │           │
│   │   │           ├── home/
│   │   │           │   ├── HomeScreen.kt
│   │   │           │   ├── HomeViewModel.kt
│   │   │           │   ├── HomeState.kt
│   │   │           │   └── components/
│   │   │           │       ├── PostItem.kt
│   │   │           │       └── PostListItem.kt
│   │   │           │
│   │   │           ├── post/
│   │   │           │   ├── detail/
│   │   │           │   │   ├── PostDetailScreen.kt
│   │   │           │   │   └── PostDetailViewModel.kt
│   │   │           │   ├── create/
│   │   │           │   │   ├── CreatePostScreen.kt
│   │   │           │   │   └── CreatePostViewModel.kt
│   │   │           │   └── edit/
│   │   │           │       ├── EditPostScreen.kt
│   │   │           │       └── EditPostViewModel.kt
│   │   │           │
│   │   │           ├── profile/
│   │   │           │   ├── ProfileScreen.kt
│   │   │           │   ├── ProfileViewModel.kt
│   │   │           │   └── components/
│   │   │           │       ├── ProfileHeader.kt
│   │   │           │       └── ProfileStats.kt
│   │   │           │
│   │   │           ├── messages/
│   │   │           │   ├── list/
│   │   │           │   │   ├── MessagesScreen.kt
│   │   │           │   │   └── MessagesViewModel.kt
│   │   │           │   └── chat/
│   │   │           │       ├── ChatScreen.kt
│   │   │           │       └── ChatViewModel.kt
│   │   │           │
│   │   │           └── settings/
│   │   │               ├── SettingsScreen.kt
│   │   │               └── SettingsViewModel.kt
│   │   │
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   ├── mipmap/
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   ├── themes.xml
│   │   │   │   └── dimens.xml
│   │   │   └── xml/
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   ├── test/                                  # Unit tests
│   │   └── java/com/kma/forumkma/
│   │       ├── domain/usecase/
│   │       ├── data/repository/
│   │       └── presentation/viewmodel/
│   │
│   └── androidTest/                           # Instrumented tests
│       └── java/com/kma/forumkma/
│           ├── di/
│           └── ui/
│
├── build.gradle.kts
└── proguard-rules.pro
```

## 🏗️ Giải Thích Các Layer

### 1️⃣ **Core Layer** (`core/`)
Chứa các class cơ bản, utilities và configuration dùng chung cho toàn app:
- **base/**: Base classes (Activity, Fragment, ViewModel, UseCase)
- **di/**: Dependency Injection modules (Hilt/Koin)
- **utils/**: Utility functions và extensions
- **network/**: Network configuration (Retrofit, OkHttp)
- **local/**: Local storage (Room Database, SharedPreferences)

### 2️⃣ **Data Layer** (`data/`)
Quản lý nguồn dữ liệu và implementations:
- **remote/**: API calls và DTOs (Data Transfer Objects)
- **local/**: Local database entities và DAOs
- **mapper/**: Convert giữa DTOs ↔️ Domain Models
- **repository/**: Implement các repository interfaces từ domain

### 3️⃣ **Domain Layer** (`domain/`)
Business logic thuần túy, không phụ thuộc Android framework:
- **model/**: Domain models (entities thuần túy)
- **repository/**: Repository interfaces (contracts)
- **usecase/**: Business rules và use cases

### 4️⃣ **Presentation Layer** (`presentation/`)
UI và logic hiển thị:
- **features/**: Các màn hình theo feature (mỗi feature có Screen + ViewModel + State)
- **navigation/**: Navigation graph
- **theme/**: Material Design theme
- **components/**: Reusable composables

## 📦 Dependencies Nên Dùng

```kotlin
// build.gradle.kts (app level)
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Retrofit (Networking)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Room (Local Database)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // DataStore (SharedPreferences replacement)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Timber (Logging)
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

## 🔄 Data Flow

```
UI (Screen) 
    ↕️
ViewModel 
    ↕️
UseCase (Domain)
    ↕️
Repository Interface (Domain)
    ↕️
Repository Implementation (Data)
    ↕️
DataSource (Remote/Local)
```

## ✅ Best Practices

1. **Single Responsibility**: Mỗi class chỉ làm 1 việc
2. **Dependency Inversion**: Domain không phụ thuộc vào Data/Presentation
3. **Naming Convention**:
   - Screen: `*Screen.kt` (VD: `LoginScreen.kt`)
   - ViewModel: `*ViewModel.kt` (VD: `LoginViewModel.kt`)
   - UseCase: `*UseCase.kt` (VD: `LoginUseCase.kt`)
   - Repository: `*Repository.kt` / `*RepositoryImpl.kt`
   - DTO: `*Dto.kt` / `*Request.kt` / `*Response.kt`
4. **State Management**: Dùng UiState sealed class cho mỗi screen
5. **Error Handling**: Dùng Result/Resource wrapper
6. **Testing**: Viết unit test cho UseCase và ViewModel

## 🚀 Các Bước Migration

1. **Backup code hiện tại**
2. **Tạo cấu trúc thư mục mới**
3. **Di chuyển code từng layer**:
   - Domain first (models, repositories, use cases)
   - Data (repository implementations, DTOs, mappers)
   - Presentation (screens, viewmodels)
4. **Setup Dependency Injection**
5. **Update imports**
6. **Test từng feature**

## 📚 Tài Liệu Tham Khảo

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVVM Pattern](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

**Lưu ý**: Đây là cấu trúc chuẩn cho dự án vừa và lớn. Với dự án nhỏ có thể đơn giản hóa bớt.
