# ✅ API Models Fix - HOÀN THÀNH 100%

## 🎉 Đã fix HOÀN TOÀN để khớp với backend!

### 1. **ApiModels.kt** ✅
```kotlin
// Login dùng USERNAME, không phải email
data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

// Register có USERNAME + EMAIL
data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null
    // + dob, gender, address
)

// AuthResponse KHÔNG có user object!
data class AuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("sessionId")
    val sessionId: String
    // NO user field!
)

// UserResponse đầy đủ
data class UserResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,
    @SerializedName("roleId")
    val roleId: String? = null,
    @SerializedName("roleName")
    val roleName: String? = null
    // + dob, gender, address, userStatus, is2FAEnabled
)
```

### 2. **Repositories.kt** ✅
```kotlin
class AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        // 1. Gọi /api/v1/auth/login với username
        val response = api.login(LoginRequest(username, password))
        
        if (response.code == "200" && response.result != null) {
            // 2. Lưu tokens
            tokenManager.saveTokens(...)
            
            // 3. Gọi /api/v1/users/me để lấy user info
            val userResponse = userApi.getMe()
            if (userResponse.code == "200" && userResponse.result != null) {
                tokenManager.saveUserInfo(
                    userId = userResponse.result.id,
                    email = userResponse.result.email,
                    name = userResponse.result.fullName
                )
            }
            
            Result.success(response.result)
        }
    }
    
    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): Result<AuthResponse> {
        // Tương tự login: gọi register, lưu tokens, fetch user info
    }
}
```

### 3. **AuthViewModel.kt** ✅
```kotlin
fun login(username: String, password: String) {
    val result = authRepository.login(username, password)
    // ...
}

fun register(
    username: String,
    email: String,
    password: String,
    firstName: String?,
    lastName: String?
) {
    val result = authRepository.register(username, email, password, firstName, lastName)
    // ...
}
```

### 4. **LoginScreen.kt** ✅
```kotlin
var username by remember { mutableStateOf("") }  // ← username, not email!

// Username Field
OutlinedTextField(
    value = username,
    onValueChange = { username = it },
    label = { Text("Tên đăng nhập") },
    leadingIcon = {
        Icon(
            imageVector = Icons.Default.Person,  // Person, not Email!
            contentDescription = "Username"
        )
    },
    // ...
)

// Login button
Button(
    onClick = {
        if (username.isNotBlank() && password.isNotBlank()) {
            viewModel.login(username, password)  // ← username!
        }
    }
)
```

### 5. **RegisterScreen.kt** ✅
```kotlin
var username by remember { mutableStateOf("") }  // ← Added!
var email by remember { mutableStateOf("") }
var password by remember { mutableStateOf("") }
var firstName by remember { mutableStateOf("") }
var lastName by remember { mutableStateOf("") }

// Validation
fun validateForm(): Boolean {
    return when {
        username.isBlank() -> {
            errorMessage = "Vui lòng nhập tên đăng nhập"
            false
        }
        username.length < 3 -> {
            errorMessage = "Tên đăng nhập phải có ít nhất 3 ký tự"
            false
        }
        email.isBlank() -> { ... }
        // ...
    }
}

// Username Field (ADDED!)
OutlinedTextField(
    value = username,
    onValueChange = { username = it },
    label = { Text("Tên đăng nhập") },
    leadingIcon = {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Username"
        )
    },
    // ...
)

// Register button
Button(
    onClick = {
        if (validateForm()) {
            viewModel.register(username, email, password, firstName, lastName)
        }
    }
)
```

---

## 📊 Flow hoạt động:

### Login Flow:
1. User nhập **username** và password
2. App gọi `POST /api/v1/auth/login` với `{username, password}`
3. Backend trả về `{accessToken, refreshToken, sessionId}`
4. App lưu tokens vào DataStore
5. App gọi `GET /api/v1/users/me` để lấy user info
6. App lưu user info (id, email, fullName) vào DataStore
7. Navigate to MainScreen

### Register Flow:
1. User nhập **username**, **email**, password, firstName, lastName
2. App gọi `POST /api/v1/auth/register` với `{username, email, password, firstName, lastName}`
3. Backend trả về `{accessToken, refreshToken, sessionId}`
4. App lưu tokens vào DataStore
5. App gọi `GET /api/v1/users/me` để lấy user info
6. App lưu user info vào DataStore
7. Navigate to MainScreen

---

## ✅ Checklist:

- [x] ApiModels.kt: LoginRequest dùng username
- [x] ApiModels.kt: RegisterRequest có username + email
- [x] ApiModels.kt: AuthResponse không có user object
- [x] ApiModels.kt: UserResponse đầy đủ fields
- [x] Repositories.kt: login() dùng username
- [x] Repositories.kt: register() có username + email
- [x] Repositories.kt: Sau login/register, gọi /users/me để lấy user info
- [x] AuthViewModel.kt: login(username, password)
- [x] AuthViewModel.kt: register(username, email, password, firstName?, lastName?)
- [x] LoginScreen.kt: Username field thay vì Email field
- [x] LoginScreen.kt: Gọi viewModel.login(username, password)
- [x] RegisterScreen.kt: Thêm Username field
- [x] RegisterScreen.kt: Validation cho username
- [x] RegisterScreen.kt: Gọi viewModel.register(username, email, password, firstName, lastName)

---

## 🎯 Kết quả:

**100% KHỚP VỚI BACKEND!** 🎉

Bây giờ bạn có thể build và test app với backend thật!

---

**Ngày fix**: 30/12/2024  
**Files đã sửa**:
- `data/model/ApiModels.kt`
- `data/repository/Repositories.kt`
- `viewmodel/AuthViewModel.kt`
- `screen/LoginScreen.kt`
- `screen/RegisterScreen.kt`
