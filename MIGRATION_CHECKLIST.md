# ✅ Clean Architecture Migration Checklist

## 📅 Timeline & Progress Tracking

### ⚙️ Phase 1: Setup & Foundation (Estimated: 1-2 days)

#### 1.1 Dependencies & Build Configuration
- [ ] Update `build.gradle.kts` (project level)
  - [ ] Add Hilt plugin
  - [ ] Update Kotlin version
  - [ ] Add KSP if needed
  
- [ ] Update `app/build.gradle.kts`
  - [ ] Add Hilt dependencies
  - [ ] Add Retrofit & OkHttp
  - [ ] Add Room dependencies
  - [ ] Add Coroutines
  - [ ] Add Compose Navigation
  - [ ] Add Timber
  - [ ] Add Coil for images
  - [ ] Enable kapt

- [ ] Sync Gradle
- [ ] Resolve any dependency conflicts

#### 1.2 Core Layer Setup
- [x] ✅ Create folder structure (`create_structure.ps1`)
- [x] ✅ Create `BaseViewModel.kt`
- [x] ✅ Create `BaseUseCase.kt` & `Resource.kt`
- [x] ✅ Create `Constants.kt`
- [x] ✅ Create `Extensions.kt`
- [x] ✅ Create `AuthInterceptor.kt`
- [x] ✅ Create `LoggingInterceptor.kt`

- [ ] Create `core/di/AppModule.kt`
  - [ ] Provide Context
  - [ ] Provide SharedPreferences
  - [ ] Provide CoroutineDispatchers

- [ ] Create `core/di/NetworkModule.kt`
  - [ ] Provide Retrofit
  - [ ] Provide OkHttpClient
  - [ ] Provide ApiService
  - [ ] Setup interceptors

- [ ] Create `core/di/DatabaseModule.kt`
  - [ ] Provide Room Database
  - [ ] Provide DAOs

- [ ] Create `core/local/SharedPrefsManager.kt`
- [ ] Create `core/local/AppDatabase.kt`
- [ ] Create `core/utils/DateUtils.kt`
- [ ] Create `core/utils/ValidationUtils.kt`
- [ ] Create `core/utils/NetworkUtils.kt`

#### 1.3 Application Setup
- [ ] Create/Update `MainApplication.kt`
  - [ ] Annotate with `@HiltAndroidApp`
  - [ ] Initialize Timber
  - [ ] Setup Coil ImageLoader

- [ ] Update `AndroidManifest.xml`
  - [ ] Set application name to `.MainApplication`
  - [ ] Add Internet permission
  - [ ] Add required permissions

---

### 🏗️ Phase 2: Domain Layer (Estimated: 2-3 days)

#### 2.1 Domain Models
- [x] ✅ Create `domain/model/User.kt`
- [x] ✅ Create `domain/model/Post.kt`
- [x] ✅ Create `domain/model/Group.kt`
- [ ] Create `domain/model/Comment.kt`
- [ ] Create `domain/model/Message.kt`
- [ ] Create `domain/model/Conversation.kt`
- [ ] Create `domain/model/Notification.kt`
- [ ] Create other models as needed

#### 2.2 Repository Interfaces
- [x] ✅ Create `domain/repository/AuthRepository.kt`
- [ ] Create `domain/repository/PostRepository.kt`
- [ ] Create `domain/repository/CommentRepository.kt`
- [ ] Create `domain/repository/UserRepository.kt`
- [ ] Create `domain/repository/GroupRepository.kt`
- [ ] Create `domain/repository/MessageRepository.kt`

#### 2.3 Use Cases - Auth
- [x] ✅ Create `domain/usecase/auth/LoginUseCase.kt`
- [ ] Create `domain/usecase/auth/RegisterUseCase.kt`
- [ ] Create `domain/usecase/auth/LogoutUseCase.kt`
- [ ] Create `domain/usecase/auth/GetCurrentUserUseCase.kt`
- [ ] Create `domain/usecase/auth/RefreshTokenUseCase.kt`

#### 2.4 Use Cases - Post
- [ ] Create `domain/usecase/post/GetPostsUseCase.kt`
- [ ] Create `domain/usecase/post/GetPostDetailUseCase.kt`
- [ ] Create `domain/usecase/post/CreatePostUseCase.kt`
- [ ] Create `domain/usecase/post/UpdatePostUseCase.kt`
- [ ] Create `domain/usecase/post/DeletePostUseCase.kt`
- [ ] Create `domain/usecase/post/LikePostUseCase.kt`
- [ ] Create `domain/usecase/post/SavePostUseCase.kt`

#### 2.5 Use Cases - Comment
- [ ] Create `domain/usecase/comment/GetCommentsUseCase.kt`
- [ ] Create `domain/usecase/comment/CreateCommentUseCase.kt`
- [ ] Create `domain/usecase/comment/UpdateCommentUseCase.kt`
- [ ] Create `domain/usecase/comment/DeleteCommentUseCase.kt`

#### 2.6 Use Cases - User
- [ ] Create `domain/usecase/user/GetUserProfileUseCase.kt`
- [ ] Create `domain/usecase/user/UpdateProfileUseCase.kt`
- [ ] Create `domain/usecase/user/FollowUserUseCase.kt`
- [ ] Create `domain/usecase/user/GetFollowersUseCase.kt`

---

### 💾 Phase 3: Data Layer (Estimated: 3-4 days)

#### 3.1 DTOs (Data Transfer Objects)
- [ ] Create `data/remote/dto/auth/`
  - [ ] `LoginRequest.kt`
  - [ ] `LoginResponse.kt`
  - [ ] `RegisterRequest.kt`
  - [ ] `UserDto.kt`

- [ ] Create `data/remote/dto/post/`
  - [ ] `PostDto.kt`
  - [ ] `CreatePostRequest.kt`
  - [ ] `UpdatePostRequest.kt`

- [ ] Create `data/remote/dto/comment/`
  - [ ] `CommentDto.kt`
  - [ ] `CreateCommentRequest.kt`

- [ ] Create other DTOs as needed

#### 3.2 Mappers
- [ ] Create `data/mapper/UserMapper.kt`
  - [ ] `toDomain(UserDto): User`
  - [ ] `toDto(User): UserDto`

- [ ] Create `data/mapper/PostMapper.kt`
- [ ] Create `data/mapper/CommentMapper.kt`
- [ ] Create other mappers as needed

#### 3.3 Remote DataSources
- [ ] Create `data/remote/datasource/AuthRemoteDataSource.kt`
  - [ ] `login(username, password): LoginResponse`
  - [ ] `register(...): UserDto`
  - [ ] `logout(): Unit`

- [ ] Create `data/remote/datasource/PostRemoteDataSource.kt`
  - [ ] `getPosts(page): List<PostDto>`
  - [ ] `getPostDetail(id): PostDto`
  - [ ] `createPost(...): PostDto`
  - [ ] `updatePost(...): PostDto`
  - [ ] `deletePost(id): Unit`

- [ ] Create `data/remote/datasource/CommentRemoteDataSource.kt`
- [ ] Create other data sources as needed

#### 3.4 Local DataSources (Room)
- [ ] Create `data/local/entities/UserEntity.kt`
- [ ] Create `data/local/entities/PostEntity.kt`
- [ ] Create `data/local/entities/CommentEntity.kt`

- [ ] Create DAOs in `core/local/dao/`
  - [ ] `UserDao.kt`
  - [ ] `PostDao.kt`
  - [ ] `CommentDao.kt`

- [ ] Create `data/local/datasource/UserLocalDataSource.kt`
- [ ] Create `data/local/datasource/PostLocalDataSource.kt`

#### 3.5 Repository Implementations
- [ ] Create `data/repository/AuthRepositoryImpl.kt`
  - [ ] Implement `AuthRepository`
  - [ ] Inject `AuthRemoteDataSource`
  - [ ] Inject `UserMapper`
  - [ ] Inject `SharedPrefsManager`

- [ ] Create `data/repository/PostRepositoryImpl.kt`
- [ ] Create `data/repository/CommentRepositoryImpl.kt`
- [ ] Create `data/repository/UserRepositoryImpl.kt`
- [ ] Create other repository implementations

#### 3.6 DI Module for Repositories
- [ ] Create `core/di/RepositoryModule.kt`
  - [ ] Bind `AuthRepository` to `AuthRepositoryImpl`
  - [ ] Bind `PostRepository` to `PostRepositoryImpl`
  - [ ] Bind other repositories

---

### 🎨 Phase 4: Presentation Layer (Estimated: 4-5 days)

#### 4.1 Navigation
- [ ] Create `presentation/navigation/Screen.kt`
  - [ ] Define sealed class for routes
  - [ ] Add all screen routes

- [ ] Create `presentation/navigation/NavGraph.kt`
  - [ ] Setup NavHost
  - [ ] Define navigation graph
  - [ ] Handle navigation arguments

- [ ] Migrate `BottomNavGraph.kt`
- [ ] Migrate `AppNavigation.kt`

#### 4.2 Theme
- [ ] Migrate `ui/theme/Color.kt` → `presentation/theme/Color.kt`
- [ ] Migrate `ui/theme/Theme.kt` → `presentation/theme/Theme.kt`
- [ ] Migrate `ui/theme/Type.kt` → `presentation/theme/Type.kt`
- [ ] Create `presentation/theme/Shape.kt`

#### 4.3 Common Components
- [ ] Create `presentation/components/CustomButton.kt`
- [ ] Create `presentation/components/CustomTextField.kt`
- [ ] Create `presentation/components/LoadingDialog.kt`
- [ ] Create `presentation/components/ErrorDialog.kt`
- [ ] Create `presentation/components/BottomNavBar.kt`
- [ ] Create `presentation/components/PostItem.kt`
- [ ] Create other reusable components

#### 4.4 Features - Splash
- [ ] Migrate `screen/SplashScreen.kt` → `presentation/features/splash/`
- [ ] Create `SplashViewModel.kt`
- [ ] Create `SplashState.kt`

#### 4.5 Features - Onboarding
- [ ] Migrate `screen/OnboardingScreen.kt` → `presentation/features/onboarding/`
- [ ] Create `OnboardingViewModel.kt`
- [ ] Create `OnboardingState.kt`

#### 4.6 Features - Auth
- [ ] Login
  - [ ] Migrate `screen/LoginScreen.kt`
  - [x] ✅ Create `LoginViewModel.kt`
  - [x] ✅ Create `LoginState.kt`
  - [ ] Refactor to use BaseViewModel
  - [ ] Inject LoginUseCase
  - [ ] Test login flow

- [ ] Register
  - [ ] Migrate `screen/RegisterScreen.kt`
  - [ ] Create `RegisterViewModel.kt`
  - [ ] Create `RegisterState.kt`
  - [ ] Inject RegisterUseCase
  - [ ] Test register flow

#### 4.7 Features - Home
- [ ] Migrate `screen/Home.kt` & `MainScreen.kt`
- [ ] Create `HomeViewModel.kt`
- [ ] Create `HomeState.kt`
- [ ] Create `components/PostItem.kt`
- [ ] Create `components/PostListItem.kt`
- [ ] Inject GetPostsUseCase
- [ ] Implement infinite scroll / pagination

#### 4.8 Features - Post
- [ ] Post Detail
  - [ ] Create `PostDetailScreen.kt`
  - [ ] Create `PostDetailViewModel.kt`
  - [ ] Create `PostDetailState.kt`

- [ ] Create Post
  - [ ] Create `CreatePostScreen.kt`
  - [ ] Create `CreatePostViewModel.kt`
  - [ ] Create `CreatePostState.kt`

- [ ] Edit Post
  - [ ] Create `EditPostScreen.kt`
  - [ ] Create `EditPostViewModel.kt`
  - [ ] Create `EditPostState.kt`

#### 4.9 Features - Profile
- [ ] Migrate `screen/Profile.kt`
- [ ] Create `ProfileViewModel.kt`
- [ ] Create `ProfileState.kt`
- [ ] Create `components/ProfileHeader.kt`
- [ ] Create `components/ProfileStats.kt`

#### 4.10 Features - Messages
- [ ] Migrate `screen/MessagesScreen.kt`
- [ ] Create `MessagesViewModel.kt`
- [ ] Create `MessagesState.kt`
- [ ] Create Chat feature
  - [ ] `ChatScreen.kt`
  - [ ] `ChatViewModel.kt`
  - [ ] `ChatState.kt`

#### 4.11 Features - Settings
- [ ] Migrate `screen/Settings.kt`
- [ ] Create `SettingsViewModel.kt`
- [ ] Create `SettingsState.kt`

#### 4.12 MainActivity
- [ ] Migrate `MainActivity.kt` to `presentation/`
- [ ] Update to use new navigation
- [ ] Annotate with `@AndroidEntryPoint`

---

### 🧪 Phase 5: Testing (Estimated: 2-3 days)

#### 5.1 Unit Tests - UseCases
- [ ] Test `LoginUseCase`
- [ ] Test `RegisterUseCase`
- [ ] Test `GetPostsUseCase`
- [ ] Test `CreatePostUseCase`

#### 5.2 Unit Tests - ViewModels
- [ ] Test `LoginViewModel`
- [ ] Test `HomeViewModel`
- [ ] Test `PostDetailViewModel`

#### 5.3 Unit Tests - Repositories
- [ ] Test `AuthRepositoryImpl`
- [ ] Test `PostRepositoryImpl`

#### 5.4 Integration Tests
- [ ] Test navigation flows
- [ ] Test DI modules
- [ ] Test database operations

---

### 🧹 Phase 6: Cleanup & Polish (Estimated: 1 day)

#### 6.1 Code Cleanup
- [ ] Remove old `com/kma/base/` code
- [ ] Update all import statements
- [ ] Remove unused files
- [ ] Clean up commented code

#### 6.2 Documentation
- [ ] Update README.md
- [ ] Add KDoc comments to public APIs
- [ ] Document complex business logic

#### 6.3 Code Quality
- [ ] Run Lint checks
- [ ] Fix all warnings
- [ ] Format code (Ctrl+Alt+L)
- [ ] Run detekt (if configured)

#### 6.4 Performance
- [ ] Profile app startup time
- [ ] Check for memory leaks
- [ ] Optimize database queries
- [ ] Add ProGuard rules

---

### ✅ Phase 7: Final Verification

- [ ] Build app successfully (Release variant)
- [ ] All features working correctly
  - [ ] Login/Register
  - [ ] Home feed
  - [ ] Post creation
  - [ ] Comments
  - [ ] Profile
  - [ ] Messages
  - [ ] Settings

- [ ] No crashes or ANRs
- [ ] Proper error handling
- [ ] Loading states working
- [ ] Navigation working smoothly
- [ ] API calls working
- [ ] Database operations working
- [ ] SharedPreferences working

---

## 📊 Progress Summary

### Overall Progress
```
Phase 1: Setup & Foundation        [████░░░░░░] 40%
Phase 2: Domain Layer              [███░░░░░░░] 30%
Phase 3: Data Layer                [░░░░░░░░░░]  0%
Phase 4: Presentation Layer        [█░░░░░░░░░] 10%
Phase 5: Testing                   [░░░░░░░░░░]  0%
Phase 6: Cleanup & Polish          [░░░░░░░░░░]  0%
Phase 7: Final Verification        [░░░░░░░░░░]  0%

Total Progress:                    [██░░░░░░░░] 15%
```

### Files Created
- ✅ Total: 13/150+ files (~9%)
- Core: 7 files ✅
- Domain: 4 files ✅
- Data: 0 files
- Presentation: 2 files ✅

---

## 📝 Notes & Issues

### Known Issues
- [ ] Issue #1: [Describe issue]
- [ ] Issue #2: [Describe issue]

### Decisions Made
- ✅ Using Hilt for DI (not Koin)
- ✅ Using Jetpack Com for UI
- ✅ Using Room for local database
- ✅ Using Retrofit for networking
- ✅ Package name: `com.kma.forumkma`

### Next Steps
1. Setup Hilt dependencies
2. Create Network & Database modules
3. Create all domain models
4. Start implementing repositories

---

**Last Updated:** [Date]
**Current Phase:** Phase 1 - Setup & Foundation
**Blocking Issues:** None
